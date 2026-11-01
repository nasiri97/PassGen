package ir.ornix.passgen.core.domain

interface AutofillService {
    fun isSupported(): Boolean
    fun isAutofillEnabled(): Boolean
    fun requestEnableAutofill()
}
