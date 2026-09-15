package ir.ornix.passgen.core.data

import android.annotation.TargetApi
import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import java.util.concurrent.Executor

class AndroidBiometricAuthenticator : BiometricAuthenticator {

    override val defaultDescription: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "Use your fingerprint or screen lock to continue"
        else "Use your fingerprint to continue"

    override fun isBiometricAvailable(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        val activity = AppContextProvider.getActivity() ?: return false
        return try {
            val biometricManager = activity.getSystemService(Context.BIOMETRIC_SERVICE)
            if (biometricManager != null) {
                val method = biometricManager.javaClass.getMethod("canAuthenticate")
                val result = method.invoke(biometricManager) as? Int
                result == 0 // BIOMETRIC_SUCCESS
            } else {
                false
            }
        } catch (e: Exception) {
            // Fallback true if service is present or just return true if P+ to allow trying
            true
        }
    }

    override fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            onError("Biometric authentication not supported on this device version.")
            return
        }

        val activity = AppContextProvider.getActivity()
        if (activity == null) {
            onError("Context Activity not available.")
            return
        }

        activity.runOnUiThread {
            try {
                val mainExecutor: Executor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    activity.mainExecutor
                } else {
                    Executor { command -> activity.runOnUiThread(command) }
                }

                val callback = @TargetApi(Build.VERSION_CODES.P)
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        activity.runOnUiThread { onSuccess() }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        activity.runOnUiThread {
                            onError(
                                errString?.toString() ?: "Authentication Error"
                            )
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        activity.runOnUiThread { onError("Authentication Failed") }
                    }
                }

                val prompt = BiometricPrompt.Builder(activity)
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)

//                prompt.setNegativeButton("Cancel", mainExecutor) { _, _ ->
//                    onError("Cancelled")
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    prompt.setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }

                prompt.build().authenticate(CancellationSignal(), mainExecutor, callback)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown biometric error")
            }
        }
    }
}
