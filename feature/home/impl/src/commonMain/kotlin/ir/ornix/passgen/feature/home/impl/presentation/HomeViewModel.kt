package ir.ornix.passgen.feature.home.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.GenerateRandomPasswordUseCase
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.masterkey.ClearMasterKeyUseCase
import ir.ornix.passgen.core.domain.masterkey.RetrieveMasterKeyUseCase
import ir.ornix.passgen.core.domain.masterkey.SaveMasterKeyUseCase
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.RemovePassGenConfigUseCase
import ir.ornix.passgen.feature.home.impl.model.PassGenWrapper
import ir.ornix.passgen.core.domain.model.Account
import ir.ornix.passgen.passwordgenerator.KDFPassGen
import ir.ornix.passgen.passwordgenerator.model.KDFPassGenConfig
import ir.ornix.passgen.passwordgenerator.model.PassGenFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HomeViewModel(
    private val addPassGenConfig: AddPassGenConfigUseCase,
    private val getAllPassGenConfigs: GetAllPassGenConfigsUseCase,
    private val removePassGenConfig: RemovePassGenConfigUseCase,
    private val generateRandomPasswordUseCase: GenerateRandomPasswordUseCase,
    private val saveMasterKeyUseCase: SaveMasterKeyUseCase,
    private val retrieveMasterKeyUseCase: RetrieveMasterKeyUseCase,
    private val clearMasterKeyUseCase: ClearMasterKeyUseCase,
    private val saveAccountUseCase: SaveAccountUseCase
) : ViewModel() {

    private val _input = MutableStateFlow("")
    private val _isAddConfigDialogVisible = MutableStateFlow(false)
    private val _passGenWrappers = MutableStateFlow<List<PassGenWrapper>>(emptyList())

    private val masterKey = retrieveMasterKeyUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<HomeUiState> = combine(
        _input,
        _isAddConfigDialogVisible,
        _passGenWrappers,
        masterKey
    ) { input, isAddVisible, wrappers, mKey ->
        HomeUiState(
            input = input,
            isAddConfigDialogVisible = isAddVisible,
            passGenWrappers = wrappers,
            isMasterKeySet = mKey != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private var calculationJob: Job? = null
    private val calculationMutex = Mutex()

    init {
        // Sync wrappers with repository
        viewModelScope.launch {
            getAllPassGenConfigs().collect { configs ->
                val currentWrappers = _passGenWrappers.value
                _passGenWrappers.value = configs.map { config ->
                    currentWrappers.find { it.passGen.passGenConfig.id == config.id }
                        ?: PassGenWrapper(KDFPassGen(config))
                }
            }
        }

        // Trigger calculations when input or wrappers change
        viewModelScope.launch {
            combine(masterKey, _input, _passGenWrappers) { mKey, inp, wrappers ->
                val feed = mKey?.let { PassGenFeed(masterKey = it, input = inp) }
                feed to wrappers
            }.collect { (feed, wrappers) ->
                wrappers.forEach { it.updateFeed(feed) }
                
                calculationJob?.cancelAndJoin()
                calculationJob = launch(Dispatchers.Default) {
                    wrappers.forEach { wrapper ->
                        ensureActive()
                        calculationMutex.withLock {
                            wrapper.generate()
                        }
                    }
                }
            }
        }
    }

    fun addConfig(config: KDFPassGenConfig) = viewModelScope.launch {
        addPassGenConfig(config)
    }

    fun removeConfig(passGenWrapper: PassGenWrapper) = viewModelScope.launch {
        removePassGenConfig(passGenWrapper.passGen.passGenConfig.id)
    }

    fun inputChanged(newInput: String) {
        _input.value = newInput
    }

    fun showAddConfigDialog() {
        _isAddConfigDialogVisible.value = true
    }

    fun hideAddConfigDialog() {
        _isAddConfigDialogVisible.value = false
    }

    fun generateRandomPassword() = generateRandomPasswordUseCase(passwordLength = 24)

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
