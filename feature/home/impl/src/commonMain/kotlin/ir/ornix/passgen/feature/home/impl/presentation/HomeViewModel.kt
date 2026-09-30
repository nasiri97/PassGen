package ir.ornix.passgen.feature.home.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllKDFPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.RemoveKDFPassGenConfigUseCase
import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val getAllPassGenConfigs: GetAllKDFPassGenConfigsUseCase,
    private val generateKDFPass: GenerateKDFPassUseCase,
    private val removePassGenConfig: RemoveKDFPassGenConfigUseCase,
    private val saveAccountUseCase: SaveAccountUseCase
) : ViewModel() {

    private val intents = Channel<HomeIntent>()

    val uiState: StateFlow<HomeUiState>
        field : MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    private val configsFlow: Flow<List<KDFPassGenConfig>> = getAllPassGenConfigs()

    private val jobs = HashMap<Int, Job>()

    private fun calculate(config: KDFPassGenConfig, input: String) {
        jobs[config.id]?.cancel()

        jobs[config.id] = viewModelScope.launch {
            apply(HomePartialState.PasswordIsCalculating(config.id))

            val password = withContext(Dispatchers.Default) {
                generateKDFPass(config, input)
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
        viewModelScope.launch {
            configsFlow.collect { configs ->
                val currentPasswordItems = uiState.value.passwordItems
                val passwordItems = mutableListOf<PasswordItem>()

                configs.forEach { config ->
                    passwordItems.add(
                        currentPasswordItems.find {
                            it.config.id == config.id
                        } ?: PasswordItem(
                            config = config,
                            password = null,
                            isCalculating = true
                        ).apply {
                            calculate(config, uiState.value.input)
                        }
                    )
                }

                apply(HomePartialState.PasswordItemsLoaded(passwordItems))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is HomeIntent.InputChanged -> {
                        uiState.value.passwordItems.forEach { passwordItem ->
                            calculate(passwordItem.config, intent.input)
                        }

                        apply(HomePartialState.InputChanged(intent.input))
                    }

                    is HomeIntent.RemoveConfig -> {
                        removePassGenConfig(intent.passGenConfig.id)
                    }

                    is HomeIntent.SaveAccount -> {
                        saveAccountUseCase(intent.account)
                    }
                }
            }
        }
    }

    /** Send new intent */
    fun dispatch(intent: HomeIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: HomePartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
