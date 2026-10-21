package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType

class IsUnlockingRequiredUseCase(
    private val isBiometricEnabledUseCase: IsBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val getLocalAuthTypeUseCase: GetLocalAuthTypeUseCase
) {

    operator fun invoke(): Boolean {
        return (getLocalAuthTypeUseCase().value != LocalAuthType.NONE) ||
                (isBiometricEnabledUseCase().value && biometricAuthenticator.isBiometricAvailable())
    }
}