package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class IsUnlockingRequiredUseCase(
    private val isFirstLaunchUseCase: IsFirstLaunchUseCase,
    private val isBiometricEnabledUseCase: IsBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val getLocalAuthTypeUseCase: GetLocalAuthTypeUseCase
) {

    operator fun invoke(): Flow<Boolean> {

        return combine(
            isFirstLaunchUseCase(),
            isBiometricEnabledUseCase(),
            biometricAuthenticator.isBiometricAvailable(),
            getLocalAuthTypeUseCase(),
        ) { isFirstLaunch, isBiometricEnabled, isBiometricAvailable, localAuthType ->

            if (isFirstLaunch) {
                false
            } else {
                (localAuthType != LocalAuthType.NONE) || (isBiometricEnabled && isBiometricAvailable)
            }
        }
    }
}