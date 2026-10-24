package ir.ornix.passgen.feature.credentials.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.credential.DeleteCredentialUseCase
import ir.ornix.passgen.core.domain.credential.GetAllCredentialsUseCase
import ir.ornix.passgen.core.domain.credential.SaveCredentialUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CredentialsViewModel(
    private val getAllCredentials: GetAllCredentialsUseCase,
    private val saveCredential: SaveCredentialUseCase,
    private val deleteCredential: DeleteCredentialUseCase,
    private val isCopyEnabled: IsCopyEnabledUseCase,
    private val getVisibilityTimeout: GetVisibilityTimeoutUseCase
) : ViewModel() {

    private val intents = Channel<CredentialsIntent>()

    val uiState: StateFlow<CredentialsUiState>
        field  : MutableStateFlow<CredentialsUiState> = MutableStateFlow(
            CredentialsUiState(
                isLoading = true
            )
        )

    init {
        observeCredentials()
        observeCopyConfig()

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is CredentialsIntent.SearchQueryChanged -> {
                        apply(CredentialsPartialState.SearchQueryChanged(intent.query))
                    }

                    is CredentialsIntent.SelectCredentialForDetail -> {
                        apply(CredentialsPartialState.CredentialSelectedForDetail(intent.credential))
                    }

                    is CredentialsIntent.SelectCredentialForEdit -> {
                        apply(CredentialsPartialState.CredentialSelectedForEdit(intent.credential))
                    }

                    is CredentialsIntent.SelectCredentialForDelete -> {
                        apply(CredentialsPartialState.CredentialSelectedForDelete(intent.credential))
                    }

                    is CredentialsIntent.UpdateCredential -> {
                        viewModelScope.launch {
                            saveCredential(intent.credential)
                            apply(CredentialsPartialState.CredentialUpdated)
                        }
                    }

                    is CredentialsIntent.DeleteCredential -> {
                        viewModelScope.launch {
                            deleteCredential(intent.credentialId)
                            apply(CredentialsPartialState.CredentialDeleted)
                        }
                    }
                }
            }
        }
    }

    private fun observeCredentials() {
        getAllCredentials()
            .onEach { credentials ->
                uiState.update { it.copy(credentials = credentials, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeCopyConfig() {
        isCopyEnabled()
            .onEach { enabled ->
                uiState.update { it.copy(isCopyEnabled = enabled) }
            }
            .launchIn(viewModelScope)

        getVisibilityTimeout()
            .onEach { timeout ->
                uiState.update { it.copy(revealTimeoutMillis = timeout.timeoutMillis) }
            }
            .launchIn(viewModelScope)
    }

    /** Send new intent */
    fun dispatch(intent: CredentialsIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: CredentialsPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
