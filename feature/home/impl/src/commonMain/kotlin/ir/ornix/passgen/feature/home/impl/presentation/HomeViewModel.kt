package ir.ornix.passgen.feature.home.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.InstalledAppsProvider
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.credential.SaveCredentialUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.GetAllKdfPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.RemoveKdfPassGenConfigUseCase
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    getAllPassGenConfigs: GetAllKdfPassGenConfigsUseCase,
    private val generateKDFPass: GenerateKDFPassUseCase,
    private val removePassGenConfig: RemoveKdfPassGenConfigUseCase,
    private val saveCredentialUseCase: SaveCredentialUseCase,
    isCopyEnabled: IsCopyEnabledUseCase,
    getVisibilityTimeout: GetVisibilityTimeoutUseCase,
    val installedAppsProvider: InstalledAppsProvider
) : ViewModel() {

    private val intents = Channel<HomeIntent>()

    internal val uiState: StateFlow<HomeUiState>
        field : MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    private val configsFlow: Flow<List<KdfPassGenConfig>> = getAllPassGenConfigs()

    private val jobs = HashMap<Long, Job>()

    private fun PassGenItem.calculate() {
        jobs[config.id]?.cancel()

        jobs[config.id] = viewModelScope.launch {
            apply(HomePartialState.PasswordIsCalculating(config.id))

            val password = withContext(Dispatchers.Default) {
                generateKDFPass(config as KdfPassGenConfig, contextInput)
            }

            currentCoroutineContext().ensureActive()

            apply(
                HomePartialState.PasswordGenerated(
                    configId = config.id,
                    password = password
                )
            )
        }
    }

    init {
        // Observe CopyEnabled
        isCopyEnabled().onEach { enabled ->
            apply(HomePartialState.CopyEnabledChanged(enabled))
        }.launchIn(viewModelScope)

        // Observe password visibility timeout
        getVisibilityTimeout().onEach { timeout ->
            apply(HomePartialState.PasswordVisibilityTimeoutChanged(timeout))
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            configsFlow.collect { configs ->
                val currentPassGenItems = uiState.value.passGenItems
                val passGenItems = mutableListOf<PassGenItem>()

                configs.forEach { config ->
                    passGenItems.add(
                        currentPassGenItems.find {
                            it.config.id == config.id
                        } ?: PassGenItem(
                            config = config,
                            contextInput = uiState.value.contextInput,
                            password = null,
                            isCalculating = true
                        ).apply {
                            calculate()
                        }
                    )
                }

                apply(HomePartialState.PasswordItemsLoaded(passGenItems))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is HomeIntent.ContextInputChanged -> {
                        apply(HomePartialState.ContextInputChanged(intent.contextInput))

                        uiState.value.passGenItems.forEach { passGenItem ->
                            passGenItem.calculate()
                        }
                    }

                    is HomeIntent.RemoveConfig -> {
                        removePassGenConfig(intent.passGenConfig.id)
                    }

                    is HomeIntent.SaveCredential -> {
                        saveCredentialUseCase(intent.credential)
                    }
                }
            }
        }
    }

    /** Send new intent */
    internal fun dispatch(intent: HomeIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: HomePartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
