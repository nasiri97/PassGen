package ir.ornix.passgen.core.domain


/**
 * A hardware-backed keyed-signing facility for computing HMAC-SHA512 signatures.
 *
 * Implementations are expected to store registered keys inside a secure
 * environment (e.g. Android's hardware-backed KeyStore, using TEE or
 * StrongBox where available) so that key material is never exposed to
 * application memory once registered, and signing operations are performed
 * inside that secure environment rather than in-process.
 */
interface HmacSigner {

    /**
     * Registers a secret for [keyId] so it can later be used to sign data.
     *
     * Before storage, [rawKey] is hashed with SHA-512, and the resulting
     * 64-byte digest — not the original [rawKey] bytes — is what is actually
     * stored as the signing key. This serves two purposes:
     * - Normalizes the key to a fixed 64-byte (512-bit) size, since the
     *   underlying secure keystore may reject arbitrary/non-standard key
     *   sizes on import.
     * - Ensures the exact original secret is never itself the value held
     *   in the keystore.
     *
     * As a result, [sign] outputs computed after registration are HMAC-SHA512
     * signatures keyed by `SHA-512(rawKey)`, not by [rawKey] directly. Callers
     * that need to verify signatures elsewhere (e.g. on a server) must apply
     * the same SHA-512 normalization to their copy of the key before computing
     * or verifying HMACs.
     *
     * Implementations must guarantee that [rawKey] (and any intermediate
     * derived value) is not retained in plain memory beyond this call —
     * e.g. by zeroing the backing byte arrays once registration completes.
     *
     * @param keyId identifier under which the derived key is registered.
     *   Calling this again with an existing [keyId] overwrites the previous key.
     * @param rawKey the original, un-normalized secret bytes to derive the
     *   signing key from. Not retained after this call returns.
     */
    fun registerKey(keyId: String, rawKey: ByteArray)

    /**
     * Produces an HMAC-SHA512 signature (MAC) over [input], using the key
     * previously registered under [keyId].
     *
     * The signature is computed using `SHA-512(rawKey)` as the HMAC key,
     * as established by [registerKey] — not the original raw key bytes.
     *
     * @param keyId identifier of a previously registered key.
     * @param input the data to sign, encoded as UTF-8.
     * @return the raw HMAC-SHA512 signature bytes (64 bytes).
     * @throws SigningKeyNotFoundException if no key exists for [keyId].
     */
    fun sign(keyId: String, input: String): ByteArray

    /**
     * Checks whether a key has been registered under [keyId].
     *
     * @param keyId identifier to check.
     * @return `true` if a key is currently registered for [keyId], `false` otherwise.
     */
    fun hasKey(keyId: String): Boolean

    /**
     * Revokes/removes the key registered under [keyId], if present.
     *
     * After this call, [sign] will throw [SigningKeyNotFoundException] for
     * the same [keyId] until [registerKey] is called again.
     *
     * @param keyId identifier of the key to remove. No-op if no key exists for it.
     */
    fun deleteKey(keyId: String)
}