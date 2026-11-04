package ir.ornix.passgen.autofill

import android.app.PendingIntent
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import ir.ornix.passgen.core.model.Credential
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PassGenAutofillService : AutofillService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @OptIn(ExperimentalGlanceRemoteViewsApi::class)
    private val glanceRemoteViews = GlanceRemoteViews()

    // Presentation cache keyed by (credentialId to fieldWidthDp)
    private val presentationCache = mutableMapOf<Pair<Long, Int>, RemoteViews>()

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback,
    ) {
        val lastContext = request.fillContexts.lastOrNull()
        if (lastContext == null) {
            callback.onSuccess(null)
            return
        }

        val parsed = StructureParser.parse(lastContext.structure, applicationContext)

        if (!parsed.hasAutofillableFields) {
            callback.onSuccess(null)
            return
        }

        val job = scope.launch {
            try {
                val matches = CredentialRepository.findMatches(
                    packageName = parsed.packageName,
                    webDomain = parsed.webDomain,
                    extractedKeywords = parsed.extractedKeywords,
                )

                if (matches.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val response = FillResponse.Builder()
                var addedCount = 0

                matches.forEach { entry ->
                    val dataset = buildDataset(entry, parsed)
                    if (dataset != null) {
                        response.addDataset(dataset)
                        addedCount++
                    }
                }

                callback.onSuccess(if (addedCount > 0) response.build() else null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
        cancellationSignal.setOnCancelListener { job.cancel() }
    }

    @OptIn(ExperimentalGlanceRemoteViewsApi::class)
    private suspend fun presentationFor(entry: Credential, widthDp: Int): RemoteViews {
        val key = entry.id to widthDp
        presentationCache[key]?.let { return it }

        val title = entry.title ?: entry.associatedWebDomains.firstOrNull()
        ?: entry.associatedPackageNames.firstOrNull() ?: "PassGen"
        val subtitle = entry.username ?: entry.email ?: entry.phone ?: ""

        val rendered = glanceRemoteViews.compose(
            context = this,
            size = DpSize(widthDp.dp, 56.dp)
        ) {
            AutofillItem(
                title = title,
                subtitle = subtitle,
                widthDp = widthDp
            )
        }.remoteViews

        presentationCache[key] = rendered
        return rendered
    }

    @Suppress("DEPRECATION")
    private suspend fun buildDataset(entry: Credential, parsed: ParsedStructure): Dataset? {
        val presentation = presentationFor(entry, parsed.targetFieldWidthDp)
        val datasetBuilder = Dataset.Builder(presentation)

        val intent = Intent(this, AutofillAuthActivity::class.java).apply {
            putExtra(AutofillAuthActivity.EXTRA_CREDENTIAL_ID, entry.id)
            putExtra(AutofillAuthActivity.EXTRA_PARSED_STRUCTURE, parsed)
        }

        val requestCode = ((entry.id * 31) + parsed.hashCode()).toInt()
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        datasetBuilder.setAuthentication(pendingIntent.intentSender)

        var hasTargetField = false
        fun associateField(id: AutofillId?) {
            if (id != null) {
                datasetBuilder.setValue(id, AutofillValue.forText(""))
                hasTargetField = true
            }
        }

        associateField(parsed.usernameId)
        associateField(parsed.emailId)
        associateField(parsed.passwordId)
        associateField(parsed.phoneId)
        parsed.genericIds.forEach { associateField(it) }

        return if (hasTargetField) datasetBuilder.build() else null
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }
}
