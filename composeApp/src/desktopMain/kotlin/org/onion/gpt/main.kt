package org.onion.gpt

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import minegpt.composeapp.generated.resources.Res
import minegpt.composeapp.generated.resources.img_user_light
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1280.dp, height = 900.dp),
        title = "MineGPT",
        icon = painterResource(Res.drawable.img_user_light)
    ) {
        App()
    }
}