package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.BiometricAuthenticator

actual fun getPlatformBiometricAuthenticator(): BiometricAuthenticator {
    return DefaultBiometricAuthenticator()
}
