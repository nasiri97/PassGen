package ir.ornix.passgen.feature.config.impl.di

import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigViewModel
import ir.ornix.passgen.feature.config.impl.random.presentation.AddRandomConfigViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val configModule = module {
    viewModelOf(::AddKdfConfigViewModel)
    viewModelOf(::AddRandomConfigViewModel)
}
