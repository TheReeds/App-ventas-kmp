package io.dev.kmpventas.presentation.screens.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.dev.kmpventas.presentation.screens.dashboard.HomeViewModel
import org.koin.compose.koinInject

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> { error("No HomeViewModel provided") }

@Composable
fun ProvideHomeViewModel(
    content: @Composable () -> Unit
) {
    val viewModel: HomeViewModel = koinInject()
    CompositionLocalProvider(LocalHomeViewModel provides viewModel) {
        content()
    }
}