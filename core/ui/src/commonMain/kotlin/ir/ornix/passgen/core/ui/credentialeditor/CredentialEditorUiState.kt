package ir.ornix.passgen.core.ui.credentialeditor

import ir.ornix.passgen.core.model.Credential

internal data class CredentialEditorUiState(
    val title: String = "",
    val description: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val associatedPackageNames: Set<String> = emptySet(),
    val associatedWebDomains: Set<String> = emptySet(),
    val associatedKeywords: Set<String> = emptySet(),
    val packageInput: String = "",
    val domainInput: String = "",
    val keywordInput: String = "",
    val isTitleTouched: Boolean = false
) {
    val isTitleValid: Boolean get() = title.isNotBlank()
    val showTitleError: Boolean get() = isTitleTouched && !isTitleValid

    companion object {
        fun fromCredential(
            credential: Credential?,
            defaultTitle: String = ""
        ): CredentialEditorUiState {
            if (credential == null) return CredentialEditorUiState(title = defaultTitle)
            return CredentialEditorUiState(
                title = credential.title ?: "",
                description = credential.description ?: "",
                username = credential.username ?: "",
                email = credential.email ?: "",
                phone = credential.phone ?: "",
                associatedPackageNames = credential.associatedPackageNames,
                associatedWebDomains = credential.associatedWebDomains,
                associatedKeywords = credential.associatedKeywords
            )
        }
    }
}