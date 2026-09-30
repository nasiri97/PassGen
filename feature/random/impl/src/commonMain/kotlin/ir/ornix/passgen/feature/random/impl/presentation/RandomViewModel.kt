package ir.ornix.passgen.feature.random.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.GetAllRandomPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.RemoveRandomPassGenConfigUseCase
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RandomViewModel(
    private val getAllPassGenConfigs: GetAllRandomPassGenConfigsUseCase,
    private val generateRandomPass: GenerateRandomPassUseCase,
    private val removePassGenConfig: RemoveRandomPassGenConfigUseCase,
    private val saveAccountUseCase: SaveAccountUseCase
) : ViewModel() {

    private val intents = Channel<RandomIntent>()

    val uiState: StateFlow<RandomUiState>
        field : MutableStateFlow<RandomUiState> = MutableStateFlow(RandomUiState())

    private val configsFlow: Flow<List<RandomPassGenConfig>> = getAllPassGenConfigs()


    private fun calculate(config: RandomPassGenConfig) = viewModelScope.launch {
        val password = withContext(Dispatchers.Default) {
            generateRandomPass(config)
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
                            password = null
                        ).apply {
                            calculate(config)
                        }
                    )
                }

                apply(RandomPartialState.PasswordItemsLoaded(passwordItems))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is RandomIntent.RemoveConfig -> {
                        removePassGenConfig(intent.passGenConfig.id)
                    }

                    is RandomIntent.SaveAccount -> {
                        saveAccountUseCase(intent.account)
                    }
                }
            }
        }
    }

    /** Send new intent */
    fun dispatch(intent: RandomIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: RandomPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
