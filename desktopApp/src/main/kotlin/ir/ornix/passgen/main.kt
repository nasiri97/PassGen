package ir.ornix.passgen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ir.ornix.passgen.composeapp.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PassGen",
    ) {
        App()
    }
}