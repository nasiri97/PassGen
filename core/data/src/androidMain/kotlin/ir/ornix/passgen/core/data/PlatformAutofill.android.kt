package ir.ornix.passgen.core.data

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.core.net.toUri
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.domain.AutofillService


class AndroidAutofillService : AutofillService {

    override fun isSupported(): Boolean {
        return true
    }

    override fun isAutofillEnabled(): Boolean {
        val context = AppContextProvider.applicationContext ?: return false
        val manager = context.getSystemService(AutofillManager::class.java) ?: return false
        return manager.hasEnabledAutofillServices()
    }

    override fun requestEnableAutofill() {
        val context = AppContextProvider.applicationContext ?: return
        val manager = context.getSystemService(AutofillManager::class.java)
        if (manager?.hasEnabledAutofillServices() == true) return

        val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
            data = "package:${context.packageName}".toUri()
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    }
}

actual fun getPlatformAutofillService(): AutofillService = AndroidAutofillService()
