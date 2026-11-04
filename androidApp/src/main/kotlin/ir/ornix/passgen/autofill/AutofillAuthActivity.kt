package ir.ornix.passgen.autofill

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.service.autofill.Dataset
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import androidx.lifecycle.lifecycleScope
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.common.Platform
import ir.ornix.passgen.core.common.platform
import ir.ornix.passgen.core.domain.credential.GetAllCredentialsUseCase
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationResult
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticator
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.auth_use_biometric
import ir.ornix.passgen.core.ui.auth_use_biometric_or_screen_lock
import ir.ornix.passgen.core.ui.auth_verify_its_you_to_autofill
import ir.ornix.passgen.core.ui.util.toStringResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AutofillAuthActivity : AppCompatActivity(), KoinComponent {

    private val getAllCredentialsUseCase: GetAllCredentialsUseCase by inject()
    private val platformAuthenticator: PlatformAuthenticator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContextProvider.setActivity(this)

        val credentialId = intent.getLongExtra(EXTRA_CREDENTIAL_ID, -1L)

        @Suppress("DEPRECATION")
        val parsedStructure = intent.getParcelableExtra<ParsedStructure>(EXTRA_PARSED_STRUCTURE)

        if (credentialId == -1L || parsedStructure == null) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                authenticateAndFill(credentialId, parsedStructure)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppContextProvider.setActivity(this)
    }

    private suspend fun authenticateAndFill(credentialId: Long, parsed: ParsedStructure) {
        val description = when (platform) {
            is Platform.Android -> {
                if ((platform as Platform.Android).apiLevel >= Build.VERSION_CODES.R) {
                    getString(Res.string.auth_use_biometric_or_screen_lock)
                } else {
                    getString(Res.string.auth_use_biometric)
                }
            }

            else -> getString(Res.string.auth_use_biometric)
        }

        platformAuthenticator.authenticate(
            title = getString(Res.string.auth_verify_its_you_to_autofill),
            subtitle = "",
            description = description,
            onResult = { result ->
                when (result) {
                    is PlatformAuthenticationResult.Success -> {
                        onAuthSuccess(credentialId, parsed)
                    }

                    is PlatformAuthenticationResult.Cancelled -> {
                        setResult(RESULT_CANCELED)
                        finish()
                    }

                    is PlatformAuthenticationResult.Error -> {
                        setResult(RESULT_CANCELED)
                        runCatching {
                            lifecycleScope.launch {
                                val errorMessage = getString(result.reason.toStringResource())
                                Toast.makeText(
                                    this@AutofillAuthActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        finish()
                    }
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    private fun onAuthSuccess(credentialId: Long, parsed: ParsedStructure) {
        lifecycleScope.launch {
            try {
                val credentials = getAllCredentialsUseCase().first()
                val entry = credentials.find { it.id == credentialId }

                if (entry == null) {
                    setResult(RESULT_CANCELED)
                    finish()
                    return@launch
                }

                val presentation = presentationFor(entry, parsed.targetFieldWidthDp)
                val datasetBuilder = Dataset.Builder(presentation)
                var hasValue = false

                fun set(id: AutofillId?, value: String?) {
                    if (id != null && !value.isNullOrEmpty()) {
                        datasetBuilder.setValue(id, AutofillValue.forText(value))
                        hasValue = true
                    }
                }

                set(parsed.usernameId, entry.username ?: entry.email)
                set(parsed.emailId, entry.email ?: entry.username)
                set(parsed.passwordId, entry.password.value.concatToString())
                set(parsed.phoneId, entry.phone)

                parsed.genericIds.forEach { genId ->
                    if (genId != parsed.usernameId &&
                        genId != parsed.emailId &&
                        genId != parsed.passwordId &&
                        genId != parsed.phoneId
                    ) {
                        set(genId, entry.username ?: entry.email)
                    }
                }

                if (hasValue) {
                    val authenticatedDataset = datasetBuilder.build()
                    val replyIntent = Intent().apply {
                        putExtra(
                            AutofillManager.EXTRA_AUTHENTICATION_RESULT,
                            authenticatedDataset as Parcelable
                        )
                    }
                    setResult(RESULT_OK, replyIntent)
                } else {
                    setResult(RESULT_CANCELED)
                }
            } catch (_: Exception) {
                setResult(RESULT_CANCELED)
            } finally {
                finish()
            }
        }
    }

    @OptIn(ExperimentalGlanceRemoteViewsApi::class)
    private suspend fun presentationFor(entry: Credential, widthDp: Int): RemoteViews {
        val glanceRemoteViews = GlanceRemoteViews()
        val title = entry.title ?: entry.associatedWebDomains.firstOrNull()
        ?: entry.associatedPackageNames.firstOrNull() ?: "PassGen"
        val subtitle = entry.username ?: entry.email ?: entry.phone ?: ""
        return glanceRemoteViews.compose(
            context = this,
            size = DpSize(widthDp.dp, 56.dp)
        ) {
            AutofillItem(title = title, subtitle = subtitle, widthDp = widthDp)
        }.remoteViews
    }

    companion object {
        const val EXTRA_CREDENTIAL_ID = "extra_credential_id"
        const val EXTRA_PARSED_STRUCTURE = "extra_parsed_structure"
    }
}
