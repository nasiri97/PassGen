package ir.ornix.passgen.feature.credentials.impl.presentation

import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

internal sealed interface CredentialsPartialState {
    data class CredentialsLoaded(val credentials: List<Credential>) : CredentialsPartialState
    data class SearchQueryChanged(val query: String) : CredentialsPartialState
    data class AddCredentialDialogVisibilityChanged(val isVisible: Boolean) :
        CredentialsPartialState

    data class CredentialSelectedForDetail(val credential: Credential?) : CredentialsPartialState
    data class CredentialSelectedForEdit(val credential: Credential?) : CredentialsPartialState
    data class CredentialSelectedForDelete(val credential: Credential?) : CredentialsPartialState
    data object CredentialCreated : CredentialsPartialState
    data object CredentialUpdated : CredentialsPartialState
    data object CredentialDeleted : CredentialsPartialState

    data class CopyEnabledChanged(val isCopyEnabled: Boolean) : CredentialsPartialState

    data class PasswordVisibilityTimeoutChanged(
        val visibilityTimeout: PasswordVisibilityTimeout
    ) : CredentialsPartialState
}