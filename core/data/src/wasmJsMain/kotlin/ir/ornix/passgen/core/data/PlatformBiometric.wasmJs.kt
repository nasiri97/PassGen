package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticator

actual fun getPlatformBiometricAuthenticator(): PlatformAuthenticator {
    return UnavailablePlatformAuthenticator()
}
