package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase

class IsUnlockingRequiredUseCase(
    private val isFirstLaunchUseCase: IsFirstLaunchUseCase,
    private val isBiometricEnabledUseCase: IsBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val getLocalAuthTypeUseCase: GetLocalAuthTypeUseCase
) {

    operator fun invoke(): Boolean {
        if (isFirstLaunchUseCase()) return false

        return (getLocalAuthTypeUseCase() == LocalAuthType.NONE) &&
                (!isBiometricEnabledUseCase() || !biometricAuthenticator.isBiometricAvailable())
    }
}