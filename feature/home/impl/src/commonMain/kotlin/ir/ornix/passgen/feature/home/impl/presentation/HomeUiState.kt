package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.PassGenItem

data class HomeUiState(
    val isLoading: Boolean = true,
    val input: String = "",
    val passGenItems: List<PassGenItem> = emptyList()
)
