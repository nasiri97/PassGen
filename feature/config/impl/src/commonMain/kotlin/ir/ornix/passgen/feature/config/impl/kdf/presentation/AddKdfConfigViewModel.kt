package ir.ornix.passgen.feature.config.impl.kdf.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.AddKdfPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.GenerateMasterKeyDigestIdUseCase
import ir.ornix.passgen.core.logging.Logger
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddKdfConfigViewModel(
    private val generateMasterKeyDigestId: GenerateMasterKeyDigestIdUseCase,
    private val addConfig: AddKdfPassGenConfigUseCase,
    private val getVisibilityTimeout: GetVisibilityTimeoutUseCase
) : ViewModel() {

    private var masterKey: CharArray = CharArray(0)
        set(value) {
            if (field !== value) field.fill('\u0000')
            field = value
        }

    private var confirmMasterKey: CharArray = CharArray(0)
        set(value) {
            if (field !== value) field.fill('\u0000')
            field = value
        }

    private val intents = Channel<AddKdfConfigIntent>()

    internal val uiState: StateFlow<AddKdfConfigUiState>
        field: MutableStateFlow<AddKdfConfigUiState> = MutableStateFlow(AddKdfConfigUiState())

    init {

        viewModelScope.launch {
            getVisibilityTimeout().collect {
                apply(AddKdfConfigPartialState.PasswordVisibilityTimeoutChanged(it))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is AddKdfConfigIntent.MasterKeyChanged -> {
                        updateMasterKey(intent.masterKey)
                    }

                    is AddKdfConfigIntent.ConfirmMasterKeyChanged -> {
                        updateConfirmMasterKey(intent.confirmMasterKey)
                    }

                    is AddKdfConfigIntent.NextStepClicked -> {
                        apply(AddKdfConfigPartialState.StepChanged(AddKdfConfigStep.ConfigDetails))
                    }

                    is AddKdfConfigIntent.PreviousStepClicked -> {
                        apply(AddKdfConfigPartialState.StepChanged(AddKdfConfigStep.MasterKey))
                    }

                    is AddKdfConfigIntent.NameChanged -> {
                        apply(AddKdfConfigPartialState.NameUpdated(intent.name))
                    }

                    is AddKdfConfigIntent.TrimSpacesToggled -> {
                        apply(AddKdfConfigPartialState.TrimSpacesUpdated(intent.enabled))
                    }

                    is AddKdfConfigIntent.CollapseSpacesToggled -> {
                        apply(AddKdfConfigPartialState.CollapseSpacesUpdated(intent.enabled))
                    }

                    is AddKdfConfigIntent.LowercaseToggled -> {
                        apply(AddKdfConfigPartialState.LowercaseUpdated(intent.enabled))
                    }

                    is AddKdfConfigIntent.HasherSelected -> {
                        val validEncoders = PassEncoder.getValidItems(intent.hasher)
                        apply(AddKdfConfigPartialState.HasherUpdated(intent.hasher, validEncoders))
                    }

                    is AddKdfConfigIntent.EncoderSelected -> {
                        apply(AddKdfConfigPartialState.EncoderUpdated(intent.encoder))
                    }

                    is AddKdfConfigIntent.PassLengthChanged -> {
                        apply(AddKdfConfigPartialState.PassLengthUpdated(intent.length))
                    }

                    is AddKdfConfigIntent.SubmitConfigClicked -> {
                        createConfig()
                    }

                    AddKdfConfigIntent.ProcessCancelled -> {
                        apply(AddKdfConfigPartialState.ProcessCancelled)
                    }
                }
            }
        }
    }

    private fun updateMasterKey(value: CharArray) {
        masterKey = value
        onKeysChanged()
    }

    private fun updateConfirmMasterKey(value: CharArray) {
        confirmMasterKey = value
        onKeysChanged()
    }

    private fun onKeysChanged() {
        val validation = validateMasterKey(masterKey, confirmMasterKey)
        apply(AddKdfConfigPartialState.MasterKeyValidationUpdated(validation))
    }

    private fun resetKeys() {
        masterKey = CharArray(0)
        confirmMasterKey = CharArray(0)
        onKeysChanged()
    }

    internal fun dispatch(intent: AddKdfConfigIntent) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun createConfig() {
        val state = uiState.value
        viewModelScope.launch {
            apply(AddKdfConfigPartialState.Submitting)
            try {
                val mkId = generateMasterKeyDigestId()

                val config = KdfPassGenConfig(
                    id = 0,
                    name = state.name.trim(),
                    masterKeyDigestId = mkId,
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

                val isConfigCreated = addConfig(config, masterKey)
                if (isConfigCreated) apply(AddKdfConfigPartialState.ConfigCreatedSuccess)
                else {
                    Logger.e("Failed to create kdf configuration")
                    apply(AddKdfConfigPartialState.ConfigCreationFailed)
                }
            } catch (e: Exception) {
                Logger.e("Failed to create kdf configuration", e)
                apply(AddKdfConfigPartialState.ConfigCreationFailed)
            }
        }
    }

    private fun apply(change: AddKdfConfigPartialState) {
        if (change is AddKdfConfigPartialState.ProcessCancelled ||
            change is AddKdfConfigPartialState.ConfigCreatedSuccess ||
            change is AddKdfConfigPartialState.ConfigCreationFailed
        ) resetKeys()

        uiState.update { reduce(it, change) }
    }
}
