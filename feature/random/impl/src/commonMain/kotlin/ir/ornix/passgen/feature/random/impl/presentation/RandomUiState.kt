package ir.ornix.passgen.feature.random.impl.presentation

data class RandomUiState(
    val isLoading: Boolean = true,
    val passwordItems: List<PasswordItem> = emptyList()
)
