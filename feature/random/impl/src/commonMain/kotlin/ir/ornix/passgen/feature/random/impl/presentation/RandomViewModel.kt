package ir.ornix.passgen.feature.random.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.credential.SaveCredentialUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.GetAllRandomPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.RemoveRandomPassGenConfigUseCase
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RandomViewModel(
    getAllPassGenConfigs: GetAllRandomPassGenConfigsUseCase,
    private val generateRandomPass: GenerateRandomPassUseCase,
    private val removePassGenConfig: RemoveRandomPassGenConfigUseCase,
    private val saveCredentialUseCase: SaveCredentialUseCase,
    private val isCopyEnabled: IsCopyEnabledUseCase,
    private val getVisibilityTimeout: GetVisibilityTimeoutUseCase
) : ViewModel() {

    private val intents = Channel<RandomIntent>()

    val uiState: StateFlow<RandomUiState>
        field : MutableStateFlow<RandomUiState> = MutableStateFlow(RandomUiState())

    private val configsFlow: Flow<List<RandomPassGenConfig>> = getAllPassGenConfigs()


    init {
        viewModelScope.launch {
            configsFlow.collect { configs ->
                apply(RandomPartialState.Refreshing)

                val currentPasswordItems = uiState.value.passGenItems
                val passGenItems = mutableListOf<PassGenItem>()

                configs.forEach { config ->
                    passGenItems.add(
                        currentPasswordItems.find {
                            it.config.id == config.id
                        } ?: PassGenItem(
                            config = config,
                            password = generateRandomPass(config),
                            isCalculating = false
                        )
                    )
                }

                apply(RandomPartialState.PasswordItemsLoaded(passGenItems))
            }
        }

        viewModelScope.launch {
            isCopyEnabled().collect {
                apply(RandomPartialState.CopyEnabledChanged(it))
            }
        }

        viewModelScope.launch {
            getVisibilityTimeout().collect {
                apply(RandomPartialState.PasswordVisibilityTimeoutChanged(it))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is RandomIntent.RefreshAllPasswords -> {
                        apply(RandomPartialState.Refreshing)

                        uiState.value.passGenItems.forEach { passwordItem ->
                            apply(
                                RandomPartialState.PasswordUpdated(
                                    configId = passwordItem.config.id,
                                    password = generateRandomPass(passwordItem.config as RandomPassGenConfig)
                                )
                            )
                        }

                        apply(RandomPartialState.Refreshed)
                    }

                    is RandomIntent.RemoveConfig -> {
                        removePassGenConfig(intent.passGenConfig.id)
                    }

                    is RandomIntent.SaveCredential -> {
                        saveCredentialUseCase(intent.credential)
                    }

                    RandomIntent.RefreshHintShown -> {
                        apply(RandomPartialState.RefreshHintShown)
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
