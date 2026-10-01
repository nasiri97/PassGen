package ir.ornix.passgen.feature.random.impl.presentation

data class RandomUiState(
    val isRefreshing: Boolean = true,
    val hasRefreshHintShown: Boolean = false,
    val passwordItems: List<PasswordItem> = emptyList()
)
