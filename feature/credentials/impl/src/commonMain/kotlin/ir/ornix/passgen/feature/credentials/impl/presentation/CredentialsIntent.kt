package ir.ornix.passgen.feature.credentials.impl.presentation

import ir.ornix.passgen.core.model.Credential

internal sealed interface CredentialsIntent {
    data class SearchQueryChanged(val query: String) : CredentialsIntent
    data object OpenAddCredentialDialog : CredentialsIntent
    data object DismissAddCredentialDialog : CredentialsIntent
    data class SelectCredentialForDetail(val credential: Credential?) : CredentialsIntent
    data class SelectCredentialForEdit(val credential: Credential?) : CredentialsIntent
    data class SelectCredentialForDelete(val credential: Credential?) : CredentialsIntent
    data class CreateCredential(val credential: Credential) : CredentialsIntent
    data class UpdateCredential(val credential: Credential) : CredentialsIntent
    data class DeleteCredential(val credentialId: Long) : CredentialsIntent
}
