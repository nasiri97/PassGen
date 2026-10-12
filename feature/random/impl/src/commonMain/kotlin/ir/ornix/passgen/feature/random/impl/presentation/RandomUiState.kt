package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

data class RandomUiState(
    val isRefreshing: Boolean = true,
    val hasRefreshHintShown: Boolean = false,
    val passGenItems: List<PassGenItem> = emptyList(),
    val isCopyEnabled: Boolean = false,
    val visibilityTimeout: PasswordVisibilityTimeout = PasswordVisibilityTimeout.default
)
