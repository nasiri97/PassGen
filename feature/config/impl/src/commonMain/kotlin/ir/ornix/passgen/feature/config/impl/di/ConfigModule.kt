package ir.ornix.passgen.feature.config.impl.di

import ir.ornix.passgen.feature.config.impl.presentation.AddConfigViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val configModule = module {
    viewModelOf(::AddConfigViewModel)
}
