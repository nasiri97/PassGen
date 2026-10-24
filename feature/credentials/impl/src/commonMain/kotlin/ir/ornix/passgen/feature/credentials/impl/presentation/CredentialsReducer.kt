package ir.ornix.passgen.feature.credentials.impl.presentation

internal fun reduce(
    oldState: CredentialsUiState,
    change: CredentialsPartialState
): CredentialsUiState {
    return when (change) {
        is CredentialsPartialState.SearchQueryChanged ->
            oldState.copy(searchQuery = change.query)

        is CredentialsPartialState.CredentialSelectedForDetail ->
            oldState.copy(selectedCredentialForDetail = change.credential)

        is CredentialsPartialState.CredentialSelectedForEdit ->
            oldState.copy(selectedCredentialForEdit = change.credential)

        is CredentialsPartialState.CredentialSelectedForDelete ->
            oldState.copy(selectedCredentialForDelete = change.credential)

        is CredentialsPartialState.CredentialUpdated ->
            oldState.copy(selectedCredentialForEdit = null)

        is CredentialsPartialState.CredentialDeleted ->
            oldState.copy(selectedCredentialForDelete = null)
    }
}