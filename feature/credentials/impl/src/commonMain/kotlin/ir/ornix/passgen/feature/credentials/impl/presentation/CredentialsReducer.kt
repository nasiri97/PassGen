package ir.ornix.passgen.feature.credentials.impl.presentation

internal fun reduce(
    oldState: CredentialsUiState,
    change: CredentialsPartialState
): CredentialsUiState {
    return when (change) {
        is CredentialsPartialState.CredentialsLoaded -> {
            oldState.copy(credentials = change.credentials, isLoading = false)
        }

        is CredentialsPartialState.SearchQueryChanged ->
            oldState.copy(searchQuery = change.query)

        is CredentialsPartialState.AddCredentialDialogVisibilityChanged ->
            oldState.copy(isAddingCredential = change.isVisible)

        is CredentialsPartialState.CredentialSelectedForDetail ->
            oldState.copy(selectedCredentialForDetail = change.credential)

        is CredentialsPartialState.CredentialSelectedForEdit ->
            oldState.copy(selectedCredentialForEdit = change.credential)

        is CredentialsPartialState.CredentialSelectedForDelete ->
            oldState.copy(selectedCredentialForDelete = change.credential)

        is CredentialsPartialState.CredentialCreated ->
            oldState.copy(isAddingCredential = false)

        is CredentialsPartialState.CredentialUpdated ->
            oldState.copy(selectedCredentialForEdit = null)

        is CredentialsPartialState.CredentialDeleted ->
            oldState.copy(selectedCredentialForDelete = null)

        is CredentialsPartialState.CopyEnabledChanged ->
            oldState.copy(isCopyEnabled = change.isCopyEnabled)

        is CredentialsPartialState.PasswordVisibilityTimeoutChanged ->
            oldState.copy(visibilityTimeout = change.visibilityTimeout)
    }
}