package ir.ornix.passgen.core.ui.credentialeditor

internal fun reduce(
    oldState: CredentialEditorUiState,
    intent: CredentialEditorIntent
): CredentialEditorUiState {
    return when (intent) {
        is CredentialEditorIntent.TitleChanged -> oldState.copy(title = intent.title)
        is CredentialEditorIntent.DescriptionChanged -> oldState.copy(description = intent.description)
        is CredentialEditorIntent.UsernameChanged -> oldState.copy(username = intent.username)
        is CredentialEditorIntent.EmailChanged -> oldState.copy(email = intent.email)
        is CredentialEditorIntent.PhoneChanged -> oldState.copy(phone = intent.phone)

        is CredentialEditorIntent.PasswordChanged -> oldState.copy(password = intent.password)

        is CredentialEditorIntent.PackageInputChanged -> oldState.copy(packageInput = intent.packageInput)
        is CredentialEditorIntent.AddPackageName -> {
            val trimmed = intent.packageName.trim()
            if (trimmed.isNotBlank()) {
                oldState.copy(
                    associatedPackageNames = oldState.associatedPackageNames + trimmed,
                    packageInput = ""
                )
            } else oldState
        }

        is CredentialEditorIntent.RemovePackageName -> oldState.copy(associatedPackageNames = oldState.associatedPackageNames - intent.packageName)

        is CredentialEditorIntent.DomainInputChanged -> oldState.copy(domainInput = intent.domainInput)
        is CredentialEditorIntent.AddWebDomain -> {
            val trimmed = intent.domain.trim()
            if (trimmed.isNotBlank()) {
                oldState.copy(
                    associatedWebDomains = oldState.associatedWebDomains + trimmed,
                    domainInput = ""
                )
            } else oldState
        }

        is CredentialEditorIntent.RemoveWebDomain -> oldState.copy(associatedWebDomains = oldState.associatedWebDomains - intent.domain)

        is CredentialEditorIntent.KeywordInputChanged -> oldState.copy(keywordInput = intent.keywordInput)
        is CredentialEditorIntent.AddKeyword -> {
            val trimmed = intent.keyword.trim()
            if (trimmed.isNotBlank()) {
                oldState.copy(
                    associatedKeywords = oldState.associatedKeywords + trimmed,
                    keywordInput = ""
                )
            } else oldState
        }

        is CredentialEditorIntent.RemoveKeyword -> oldState.copy(associatedKeywords = oldState.associatedKeywords - intent.keyword)

        is CredentialEditorIntent.TitleBlurred -> oldState.copy(isTitleTouched = true)
        is CredentialEditorIntent.PasswordBlurred -> oldState.copy(isPasswordTouched = true)
    }
}
