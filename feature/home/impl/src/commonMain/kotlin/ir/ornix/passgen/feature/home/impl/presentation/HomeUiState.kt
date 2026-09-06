package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.feature.home.impl.model.PassGenWrapper

data class HomeUiState(
    val input: String = "",
    val passGenWrappers: List<PassGenWrapper> = emptyList(),
    val isAddConfigDialogVisible: Boolean = false,
    val isMasterKeySet: Boolean = false
)
