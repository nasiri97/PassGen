package ir.ornix.passgen.feature.setup.impl.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ir.ornix.passgen.core.domain.MasterKeyRepository

class SetupViewModel(private val repository: MasterKeyRepository) : ViewModel() {
    var masterKey by mutableStateOf("")
        private set

    fun onKeyChange(newValue: String) {
        masterKey = newValue
    }

    fun saveAndContinue(onSuccess: () -> Unit) {
        if (masterKey.isNotBlank()) {
            repository.saveMasterKey(masterKey)
            onSuccess()
        }
    }
}
