package ir.ornix.passgen.core.data

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.domain.platformauth.AuthenticationAvailability
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationFailureReason
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationResult
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticator
import ir.ornix.passgen.core.logging.Logger
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class AndroidPlatformAuthenticator : PlatformAuthenticator {

    private val authenticators: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_WEAK or DEVICE_CREDENTIAL
        }

    private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findFragmentActivity()
        else -> null
    }

    override fun isAvailable(): AuthenticationAvailability =
        AppContextProvider.applicationContext?.checkAuthenticationAvailability(authenticators)
            ?: run {
                Logger.i("Biometric prompt requires an application context, but it is null.")
                AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.Unknown)
            }

    @OptIn(ExperimentalAtomicApi::class)
    override fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        onResult: (PlatformAuthenticationResult) -> Unit
    ) {
        val activity = AppContextProvider.getActivity()?.findFragmentActivity() ?: run {
            Logger.i("Biometric prompt requires a FragmentActivity")
            return onResult(PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.Unknown))
        }

        when (val availability = activity.checkAuthenticationAvailability(authenticators)) {
            is AuthenticationAvailability.Available -> Unit
            is AuthenticationAvailability.Unavailable ->
                return onResult(PlatformAuthenticationResult.Error(availability.reason))
        }

        val delivered = AtomicBoolean(false)
        val deliver: (result: PlatformAuthenticationResult) -> Unit = { result ->
            if (delivered.compareAndSet(expectedValue = false, newValue = true)) {
                try {
                    onResult(result)
                } catch (e: Exception) {
                    Logger.e("onResult threw", e)
                }
            }
        }

        activity.runOnUiThread {
            showPrompt(
                activity = activity,
                title = title,
                subtitle = subtitle,
                description = description,
                onResult = deliver
            )
        }
    }

    private fun showPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String,
        onResult: (PlatformAuthenticationResult) -> Unit
    ) {
        if (activity.isFinishing || activity.isDestroyed || activity.supportFragmentManager.isStateSaved)
            return onResult(PlatformAuthenticationResult.Cancelled)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(PlatformAuthenticationResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                val result = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_CANCELED -> {
                        PlatformAuthenticationResult.Cancelled
                    }

                    BiometricPrompt.ERROR_LOCKOUT -> {
                        Logger.i("Too many attempts. Try again in a moment.")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.TemporarilyLocked)
                    }

                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        Logger.i("Biometrics are locked. Unlock your device with your screen lock first.")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.PermanentlyLocked)
                    }

                    BiometricPrompt.ERROR_NO_BIOMETRICS,
                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                        Logger.i("No fingerprint or screen lock set up")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.NotEnrolled)
                    }

                    BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                        Logger.i("Biometric sensor is not present")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.NotAvailable)
                    }

                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                        Logger.i("Biometric sensor is currently unavailable")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.HardwareUnavailable)
                    }

                    BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> {
                        Logger.i("A security update is required for Biometric authentication")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.SecurityUpdateRequired)
                    }

                    else -> {
                        Logger.i("$errString (error-code: $errorCode)")
                        PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.Unknown)
                    }
                }

                onResult(result)
            }

            // onAuthenticationFailed() = unrecognized finger, the prompt stays open for a retry.
        }

        try {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(authenticators)
                .setConfirmationRequired(false) // only matters for face/iris, harmless for fingerprint
                .build()

            BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                callback
            ).authenticate(promptInfo)
        } catch (e: Exception) {
            Logger.e("Authentication failed", e)
            onResult(PlatformAuthenticationResult.Error(PlatformAuthenticationFailureReason.Unknown))
        }
    }
}


fun Context.checkAuthenticationAvailability(authenticators: Int): AuthenticationAvailability =
    try {
        when (BiometricManager.from(this).canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> AuthenticationAvailability.Available

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Logger.i("No fingerprint sensor available")
                AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.NotAvailable)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Logger.i("Fingerprint sensor is currently unavailable")
                AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.HardwareUnavailable)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Logger.i("No fingerprint enrolled")
                AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.NotEnrolled)
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Logger.i("A security update is required")
                AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.SecurityUpdateRequired)
            }

            else -> {
                Logger.i("Biometric authentication is not available")
                AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.Unknown)
            }
        }
    } catch (e: Exception) {
        Logger.e("Biometric authentication is not available", e)
        AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.Unknown)
    }

