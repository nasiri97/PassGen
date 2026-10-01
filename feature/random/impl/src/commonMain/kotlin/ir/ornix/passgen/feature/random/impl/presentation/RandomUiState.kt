package ir.ornix.passgen.feature.random.impl.presentation

data class RandomUiState(
    val isRefreshing: Boolean = true,
    val passwordItems: List<PasswordItem> = emptyList()
)
