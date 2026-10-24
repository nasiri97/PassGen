package ir.ornix.passgen.autofill

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

    // Composing is not free and autofill has a short timeout -> cache per entry.
    private val presentationCache = mutableMapOf<Long, RemoteViews>()

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback,
    ) {
        val parsed = StructureParser.parse(request.fillContexts.last().structure)

        if (!parsed.isLoginForm) {
            callback.onSuccess(null)
            return
        }

        val job = scope.launch {
            try {
                val matches =
                    CredentialRepository.findMatches(parsed.packageName, parsed.webDomain, null)
                if (matches.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val response = FillResponse.Builder()
                var added = false
                matches.forEach { entry ->
                    buildDataset(entry, parsed)?.let {
                        response.addDataset(it)
                        added = true
                    }
                }
                callback.onSuccess(if (added) response.build() else null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
        cancellationSignal.setOnCancelListener { job.cancel() }
    }

    @OptIn(ExperimentalGlanceRemoteViewsApi::class)
    private suspend fun presentationFor(entry: Credential): RemoteViews =
        presentationCache[entry.id] ?: glanceRemoteViews.compose(
            context = this,
            size = DpSize(256.dp, 56.dp)
        ) {
            AutofillItem(
                title = entry.title ?: "",
                subtitle = entry.username ?: entry.email ?: entry.phone ?: ""
            )
        }.remoteViews.also { presentationCache[entry.id] = it }

    @Suppress("DEPRECATION") // Dataset.Builder(RemoteViews) works on API 26+
    private suspend fun buildDataset(entry: Credential, p: ParsedStructure): Dataset? {
        val builder = Dataset.Builder(presentationFor(entry))
        var hasValue = false

        fun set(id: AutofillId?, value: String?) {
            if (id != null && value != null) {
                builder.setValue(id, AutofillValue.forText(value))
                hasValue = true
            }
        }

        set(p.usernameId, entry.username ?: entry.email)
        set(p.emailId, entry.email ?: entry.username)
        set(p.passwordId, entry.password.value.concatToString())
        set(p.phoneId, entry.phone)

        // Dataset.Builder.build() throws if no value was set
        return if (hasValue) builder.build() else null
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }
}