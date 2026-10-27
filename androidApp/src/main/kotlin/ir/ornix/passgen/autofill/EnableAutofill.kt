package ir.ornix.passgen.autofill

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.autofill.AutofillManager

fun Activity.requestEnableAutofill() {
    val manager = getSystemService(AutofillManager::class.java)
    if (manager?.hasEnabledAutofillServices() == true) return

    val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
        data = Uri.parse("package:$packageName")
    }
    startActivity(intent)
}