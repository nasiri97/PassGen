package ir.ornix.passgen.feature.config.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddConfigViewModel(
    private val addPassGenConfigUseCase: AddPassGenConfigUseCase
) : ViewModel() {

    private val intents = Channel<AddConfigIntent>()

    val uiState: StateFlow<AddConfigUiState>
        field: MutableStateFlow<AddConfigUiState> = MutableStateFlow(AddConfigUiState())

    init {
        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is AddConfigIntent.MasterKeyChanged -> {
                        apply(AddConfigPartialState.MasterKeyUpdated(intent.masterKey))
                    }
                    is AddConfigIntent.ConfirmMasterKeyChanged -> {
                        apply(AddConfigPartialState.ConfirmMasterKeyUpdated(intent.confirmMasterKey))
                    }
                    is AddConfigIntent.NextStepClicked -> {
                        apply(AddConfigPartialState.StepChanged(AddConfigStep.ConfigDetails))
                    }
                    is AddConfigIntent.PreviousStepClicked -> {
                        apply(AddConfigPartialState.StepChanged(AddConfigStep.MasterKey))
                    }
                    is AddConfigIntent.NameChanged -> {
                        apply(AddConfigPartialState.NameUpdated(intent.name))
                    }
                    is AddConfigIntent.TrimSpacesToggled -> {
                        apply(AddConfigPartialState.TrimSpacesUpdated(intent.enabled))
                    }
                    is AddConfigIntent.CollapseSpacesToggled -> {
                        apply(AddConfigPartialState.CollapseSpacesUpdated(intent.enabled))
                    }
                    is AddConfigIntent.LowercaseToggled -> {
                        apply(AddConfigPartialState.LowercaseUpdated(intent.enabled))
                    }
                    is AddConfigIntent.HasherSelected -> {
                        val validEncoders = PassEncoder.getValidItems(intent.hasher)
                        apply(AddConfigPartialState.HasherUpdated(intent.hasher, validEncoders))
                    }
                    is AddConfigIntent.EncoderSelected -> {
                        apply(AddConfigPartialState.EncoderUpdated(intent.encoder))
                    }
                    is AddConfigIntent.PassLengthChanged -> {
                        apply(AddConfigPartialState.PassLengthUpdated(intent.length))
                    }
                    is AddConfigIntent.SubmitConfigClicked -> {
                        createConfig()
                    }
                }
            }
        }
    }

    fun dispatch(intent: AddConfigIntent) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun createConfig() {
        val state = uiState.value
        viewModelScope.launch {
            apply(AddConfigPartialState.Submitting)
            try {
                val config = KDFPassGenConfig(
                    id = 0,
                    name = state.name.trim(),
                    passEncoder = state.selectedEncoder,
                    passwordLength = when (state.selectedEncoder) {
                        is SeedPassEncoder -> null
                        is StringPassEncoder -> state.passLength
                    },
                    preprocessConfig = PreprocessConfig(
                        trimLeadingAndTrailingSpaces = state.trimSpaces,
                        collapseMultipleSpaces = state.collapseSpaces,
                        convertToLowercase = state.lowercase
                    ),
                    inputHasher = state.selectedHasher
                )
                addPassGenConfigUseCase(config, state.masterKey.encodeToByteArray())
                apply(AddConfigPartialState.ConfigCreatedSuccess)
            } catch (e: Exception) {
                apply(AddConfigPartialState.ConfigCreatedError(e.message ?: "Failed to create configuration"))
            }
        }
    }

    private fun apply(change: AddConfigPartialState) {
        uiState.update { reduce(it, change) }
    }
}
