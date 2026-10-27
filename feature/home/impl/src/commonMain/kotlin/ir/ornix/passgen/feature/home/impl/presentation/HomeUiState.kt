package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

internal data class HomeUiState(
    val isLoading: Boolean = true,
    val input: String = "",
    val passGenItems: List<PassGenItem> = emptyList(),
    val isCopyEnabled: Boolean = false,
    val visibilityTimeout: PasswordVisibilityTimeout = PasswordVisibilityTimeout.default
)
