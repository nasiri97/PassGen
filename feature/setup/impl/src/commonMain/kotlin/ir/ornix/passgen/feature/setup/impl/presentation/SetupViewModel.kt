package ir.ornix.passgen.feature.setup.impl.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.masterkey.SaveMasterKeyUseCase
import kotlinx.coroutines.launch

class SetupViewModel(private val saveMasterKeyUseCase: SaveMasterKeyUseCase) : ViewModel() {
    var masterKey by mutableStateOf("")
        private set

    fun onKeyChange(newValue: String) {
        masterKey = newValue
    }

    fun saveAndContinue(onSuccess: () -> Unit) {
        if (masterKey.isNotBlank()) {
            viewModelScope.launch {
                saveMasterKeyUseCase(masterKey)
                onSuccess()
            }
        }
    }
}
