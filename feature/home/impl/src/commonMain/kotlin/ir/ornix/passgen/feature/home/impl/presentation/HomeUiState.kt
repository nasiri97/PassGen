package ir.ornix.passgen.feature.home.impl.presentation

data class HomeUiState(
    val isLoading: Boolean = true,
    val input: String = "",
    val passwordItems: List<PasswordItem> = emptyList(),
    val isAddConfigDialogVisible: Boolean = false
)
