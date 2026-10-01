package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.PassGenItem

data class RandomUiState(
    val isRefreshing: Boolean = true,
    val hasRefreshHintShown: Boolean = false,
    val passGenItems: List<PassGenItem> = emptyList()
)
