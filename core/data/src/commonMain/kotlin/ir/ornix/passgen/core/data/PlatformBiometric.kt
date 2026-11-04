package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticator

expect fun getPlatformBiometricAuthenticator(): PlatformAuthenticator
