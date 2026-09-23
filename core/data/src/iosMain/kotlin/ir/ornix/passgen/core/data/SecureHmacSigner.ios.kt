package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreCrypto.CCHmac
import platform.CoreCrypto.CC_SHA512
import platform.CoreCrypto.CC_SHA512_DIGEST_LENGTH
import platform.CoreCrypto.kCCHmacAlgSHA512
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSCopyingProtocol
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSNumber
import platform.Foundation.create
import platform.Foundation.numberWithBool
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class SecureHmacSigner : HmacSigner {

    private companion object {
        const val KEYCHAIN_SERVICE = "ir.ornix.passgen.hmac"
    }

    actual override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
        val mkd = ByteArray(CC_SHA512_DIGEST_LENGTH)
        try {
            rawMasterKey.usePinned { rawPinned ->
                mkd.usePinned { mkdPinned ->
                    CC_SHA512(
                        rawPinned.addressOf(0),
                        rawMasterKey.size.convert(),
                        mkdPinned.addressOf(0).reinterpret()
                    )
                }
            }
            saveToKeychain(mkdId, mkd)
        } finally {
            rawMasterKey.fill(0)
            mkd.fill(0)
        }
    }

    actual override suspend fun sign(mkdId: String, input: String): ByteArray {
        val mkd = loadFromKeychain(mkdId) ?: throw SigningKeyNotFoundException(mkdId)
        val inputBytes = input.encodeToByteArray()
        val mac = ByteArray(CC_SHA512_DIGEST_LENGTH)
        try {
            mkd.usePinned { mkdPinned ->
                inputBytes.usePinned { inputPinned ->
                    mac.usePinned { macPinned ->
                        CCHmac(
                            kCCHmacAlgSHA512,
                            mkdPinned.addressOf(0),
                            mkd.size.convert(),
                            inputPinned.addressOf(0),
                            inputBytes.size.convert(),
                            macPinned.addressOf(0)
                        )
                    }
                }
            }
            return mac
        } finally {
            mkd.fill(0)
        }
    }

    actual override suspend fun hasMasterKeyDigest(mkdId: String): Boolean {
        val query = keyChainQuery(mkdId)
        query.setObject(
            NSNumber.numberWithBool(false),
            forKey = kSecReturnData as NSCopyingProtocol
        )
        val status = SecItemCopyMatching(query as CFDictionaryRef, null)
        return status == errSecSuccess
    }

    actual override suspend fun deleteMasterKeyDigest(mkdId: String) {
        val query = keyChainQuery(mkdId)
        SecItemDelete(query as CFDictionaryRef)
    }

    private fun keyChainQuery(mkdId: String): NSMutableDictionary {
        val query = NSMutableDictionary()
        query.setObject(kSecClassGenericPassword, forKey = kSecClass as NSCopyingProtocol)
        query.setObject(KEYCHAIN_SERVICE, forKey = kSecAttrService as NSCopyingProtocol)
        query.setObject(mkdId, forKey = kSecAttrAccount as NSCopyingProtocol)
        return query
    }

    private suspend fun saveToKeychain(mkdId: String, mkd: ByteArray) {
        deleteMasterKeyDigest(mkdId)

        val query = keyChainQuery(mkdId)
        val nsData = NSData.create(
            bytes = mkd.usePinned { it.addressOf(0) },
            length = mkd.size.convert()
        )
        query.setObject(nsData, forKey = kSecValueData as NSCopyingProtocol)
        query.setObject(
            kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly,
            forKey = kSecAttrAccessible as NSCopyingProtocol
        )

        SecItemAdd(query as CFDictionaryRef, null)
    }

    private fun loadFromKeychain(mkdId: String): ByteArray? {
        val query = keyChainQuery(mkdId)
        query.setObject(NSNumber.numberWithBool(true), forKey = kSecReturnData as NSCopyingProtocol)
        query.setObject(kSecMatchLimitOne, forKey = kSecMatchLimit as NSCopyingProtocol)

        return memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)
            if (status == errSecSuccess && result.value != null) {
                val nsData = CFBridgingRelease(result.value) as? NSData
                if (nsData != null && nsData.length > 0u) {
                    val bytes = ByteArray(nsData.length.toInt())
                    bytes.usePinned { pinned ->
                        memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                    }
                    bytes
                } else null
            } else null
        }
    }
}
