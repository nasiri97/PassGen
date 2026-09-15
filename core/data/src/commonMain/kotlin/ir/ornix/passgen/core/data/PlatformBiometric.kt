package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.BiometricAuthenticator

expect fun getPlatformBiometricAuthenticator(): BiometricAuthenticator
