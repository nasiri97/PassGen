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
    getAllCredentials: GetAllCredentialsUseCase,
    private val saveCredential: SaveCredentialUseCase,
    private val deleteCredential: DeleteCredentialUseCase,
    isCopyEnabled: IsCopyEnabledUseCase,
    getVisibilityTimeout: GetVisibilityTimeoutUseCase
) : ViewModel() {

    private val intents = Channel<CredentialsIntent>()

    internal val uiState: StateFlow<CredentialsUiState>
        field: MutableStateFlow<CredentialsUiState> = MutableStateFlow(CredentialsUiState())

    init {
        // Observe Credentials
        getAllCredentials().onEach { credentials ->
            apply(CredentialsPartialState.CredentialsLoaded(credentials))
        }.launchIn(viewModelScope)

        // Observe CopyEnabled
        isCopyEnabled().onEach { enabled ->
            apply(CredentialsPartialState.CopyEnabledChanged(enabled))
        }.launchIn(viewModelScope)

        // Observe password visibility timeout
        getVisibilityTimeout().onEach { timeout ->
            apply(CredentialsPartialState.PasswordVisibilityTimeoutChanged(timeout))
        }.launchIn(viewModelScope)


        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is CredentialsIntent.SearchQueryChanged -> {
                        apply(CredentialsPartialState.SearchQueryChanged(intent.query))
                    }

                    is CredentialsIntent.OpenAddCredentialDialog -> {
                        apply(CredentialsPartialState.AddCredentialDialogVisibilityChanged(isVisible = true))
                    }

                    is CredentialsIntent.DismissAddCredentialDialog -> {
                        apply(CredentialsPartialState.AddCredentialDialogVisibilityChanged(isVisible = false))
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

                    is CredentialsIntent.CreateCredential -> {
                        viewModelScope.launch {
                            saveCredential(intent.credential)
                            apply(CredentialsPartialState.CredentialCreated)
                        }
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


    /** Send new intent */
    internal fun dispatch(intent: CredentialsIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: CredentialsPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
