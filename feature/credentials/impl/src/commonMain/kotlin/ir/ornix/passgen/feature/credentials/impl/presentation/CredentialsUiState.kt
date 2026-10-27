package ir.ornix.passgen.feature.credentials.impl.presentation

import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

internal data class CredentialsUiState(
    val credentials: List<Credential> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isAddingCredential: Boolean = false,
    val selectedCredentialForDetail: Credential? = null,
    val selectedCredentialForEdit: Credential? = null,
    val selectedCredentialForDelete: Credential? = null,
    val isCopyEnabled: Boolean = false,
    val visibilityTimeout: PasswordVisibilityTimeout = PasswordVisibilityTimeout.default
) {
    val filteredCredentials: List<Credential>
        get() {
            if (searchQuery.isBlank()) return credentials
            val query = searchQuery.trim().lowercase()
            return credentials.filter { credential ->
                (credential.title?.lowercase()?.contains(query) == true) ||
                        (credential.description?.lowercase()?.contains(query) == true) ||
                        (credential.username?.lowercase()?.contains(query) == true) ||
                        (credential.email?.lowercase()?.contains(query) == true) ||
                        (credential.phone?.lowercase()?.contains(query) == true) ||
                        credential.associatedPackageNames.any { it.lowercase().contains(query) } ||
                        credential.associatedWebDomains.any { it.lowercase().contains(query) } ||
                        credential.associatedKeywords.any { it.lowercase().contains(query) }
            }
        }
}
