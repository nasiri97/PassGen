package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class IsUnlockingRequiredUseCase(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val isBiometricEnabled: IsBiometricEnabledUseCase,
    private val getLocalAuthType: GetLocalAuthTypeUseCase
) {
    operator fun invoke(): Flow<Boolean> =
        combine(
            getLocalAuthType(),
            isBiometricEnabled()
        ) { localAuthType, isBiometricEnabled ->
            localAuthType != LocalAuthType.NONE ||
                    (isBiometricEnabled && biometricAuthenticator.isBiometricAvailable())
        }
}