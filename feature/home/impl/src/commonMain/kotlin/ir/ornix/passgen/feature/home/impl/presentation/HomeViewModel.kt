package ir.ornix.passgen.feature.home.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgen.model.PassGenWrapper
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.masterkey.ClearMasterKeyUseCase
import ir.ornix.passgen.core.domain.masterkey.SaveMasterKeyUseCase
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.RemovePassGenConfigUseCase
import ir.ornix.passgen.core.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val generateKDFPassUseCase: GenerateKDFPassUseCase,
    private val addPassGenConfig: AddPassGenConfigUseCase,
    private val removePassGenConfig: RemovePassGenConfigUseCase,
    private val generateRandomPassUseCase: GenerateRandomPassUseCase,
    private val saveMasterKeyUseCase: SaveMasterKeyUseCase,
    private val clearMasterKeyUseCase: ClearMasterKeyUseCase,
    private val saveAccountUseCase: SaveAccountUseCase
) : ViewModel() {

    val input: StateFlow<String>
        field = MutableStateFlow("")


    val isAddConfigDialogVisible: StateFlow<Boolean>
        field = MutableStateFlow(false)


    private val passGenWrappers: Flow<List<PassGenWrapper>> = generateKDFPassUseCase(input = input)

    init {
        viewModelScope.launch {
            passGenWrappers.collect()
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        input,
        isAddConfigDialogVisible,
        passGenWrappers
    ) { input, isAddVisible, wrappers ->
        HomeUiState(
            input = input,
            isAddConfigDialogVisible = isAddVisible,
            passGenWrappers = wrappers,
            isMasterKeySet = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())


    fun addConfig(config: KDFPassGenConfig) = viewModelScope.launch {
        addPassGenConfig(config)
    }

    fun removeConfig(passGenWrapper: PassGenWrapper) = viewModelScope.launch {
        removePassGenConfig(passGenWrapper.passGenConfig.id)
    }

    fun inputChanged(newInput: String) {
        input.value = newInput
    }

    fun showAddConfigDialog() {
        isAddConfigDialogVisible.value = true
    }

    fun hideAddConfigDialog() {
        isAddConfigDialogVisible.value = false
    }

    fun generateRandomPassword() = generateRandomPassUseCase(passwordLength = 24)

    fun saveMasterKey(key: String) = viewModelScope.launch {
        saveMasterKeyUseCase(key)
    }

    fun clearMasterKey() = viewModelScope.launch {
        clearMasterKeyUseCase()
    }

    fun saveAccount(account: Account) = viewModelScope.launch {
        saveAccountUseCase(account)
    }
}
