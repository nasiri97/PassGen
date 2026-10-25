package ir.ornix.passgen.core.ui.credentialeditor

import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.Password

internal data class CredentialEditorUiState(
    val title: String = "",
    val description: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val password: Password? = null,
    val isPasswordTouched: Boolean = false,
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

    val isPasswordValid: Boolean get() = password?.value?.isNotEmpty() == true
    val showPasswordError: Boolean get() = isPasswordTouched && !isPasswordValid

    fun isSaveEnabled(): Boolean {
        return isTitleValid && isPasswordValid
    }

    companion object {
        fun fromCredential(
            credential: Credential?,
            defaultTitle: String = "",
            initialPassword: Password? = null
        ): CredentialEditorUiState {
            val initialPassword = initialPassword ?: credential?.password

            return if (credential == null)
                CredentialEditorUiState(
                    title = defaultTitle,
                    password = initialPassword
                )
            else
                CredentialEditorUiState(
                    title = credential.title ?: "",
                    description = credential.description ?: "",
                    username = credential.username ?: "",
                    email = credential.email ?: "",
                    phone = credential.phone ?: "",
                    password = initialPassword,
                    associatedPackageNames = credential.associatedPackageNames,
                    associatedWebDomains = credential.associatedWebDomains,
                    associatedKeywords = credential.associatedKeywords
                )
        }
    }
}