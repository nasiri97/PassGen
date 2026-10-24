package ir.ornix.passgen.feature.credentials.impl.presentation

import ir.ornix.passgen.core.model.Credential

internal sealed interface CredentialsPartialState {
    data class SearchQueryChanged(val query: String) : CredentialsPartialState
    data class CredentialSelectedForDetail(val credential: Credential?) : CredentialsPartialState
    data class CredentialSelectedForEdit(val credential: Credential?) : CredentialsPartialState
    data class CredentialSelectedForDelete(val credential: Credential?) : CredentialsPartialState
    data object CredentialUpdated : CredentialsPartialState
    data object CredentialDeleted : CredentialsPartialState
}