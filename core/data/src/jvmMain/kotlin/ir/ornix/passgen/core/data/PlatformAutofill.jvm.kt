package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.AutofillService

private object NoOpAutofillService : AutofillService {
    override fun isSupported(): Boolean = false
    override fun isAutofillEnabled(): Boolean = true
    override fun requestEnableAutofill() {}
}

actual fun getPlatformAutofillService(): AutofillService = NoOpAutofillService
