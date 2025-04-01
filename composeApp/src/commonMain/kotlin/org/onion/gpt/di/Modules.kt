package org.onion.gpt.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.onion.gpt.ui.screen.home.ChatViewModel


val provideViewModelModule = module {
    // ViewModels
    viewModel { ChatViewModel() }
}

fun appModule() = listOf(provideViewModelModule)