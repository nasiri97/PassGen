package ir.ornix.passgen.feature.config.impl.random.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.passgenconfig.random.AddRandomPassGenConfigUseCase
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRandomConfigViewModel(
    private val addConfig: AddRandomPassGenConfigUseCase
) : ViewModel() {

    private val intents = Channel<AddRandomConfigIntent>()

    val uiState: StateFlow<AddRandomConfigUiState>
        field: MutableStateFlow<AddRandomConfigUiState> = MutableStateFlow(AddRandomConfigUiState())

    init {
        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is AddRandomConfigIntent.NameChanged -> {
                        apply(AddRandomConfigPartialState.NameUpdated(intent.name))
                    }

                    is AddRandomConfigIntent.EncoderSelected -> {
                        apply(AddRandomConfigPartialState.EncoderUpdated(intent.encoder))
                    }

                    is AddRandomConfigIntent.PassLengthChanged -> {
                        apply(AddRandomConfigPartialState.PassLengthUpdated(intent.length))
                    }

                    is AddRandomConfigIntent.SubmitConfigClicked -> {
                        createConfig()
                    }
                }
            }
        }
    }

    fun dispatch(intent: AddRandomConfigIntent) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun createConfig() {
        val state = uiState.value
        viewModelScope.launch {
            apply(AddRandomConfigPartialState.Submitting)
            try {
                val config = RandomPassGenConfig(
                    id = 0,
                    name = state.name.trim(),
                    passEncoder = state.selectedEncoder,
                    passwordLength = when (state.selectedEncoder) {
                        is SeedPassEncoder -> null
                        is StringPassEncoder -> state.passLength
                    }
                )
                addConfig(config)
                apply(AddRandomConfigPartialState.ConfigCreatedSuccess)
            } catch (e: Exception) {
                apply(
                    AddRandomConfigPartialState.ConfigCreatedError(
                        e.message ?: "Failed to create configuration"
                    )
                )
            }
        }
    }

    private fun apply(change: AddRandomConfigPartialState) {
        uiState.update { reduce(it, change) }
    }
}
