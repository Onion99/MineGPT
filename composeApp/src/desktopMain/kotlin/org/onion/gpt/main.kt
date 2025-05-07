package org.onion.gpt

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import minegpt.composeapp.generated.resources.Res
import minegpt.composeapp.generated.resources.img_user_light
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit
import kotlin.math.sqrt

fun main() = application {
    // 获取系统屏幕尺寸
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    
    val windowWidth = (screenSize.width * 0.5).toInt().dp
    val windowHeight = (screenSize.height * 0.7).toInt().dp
    
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = windowWidth, height = windowHeight),
        title = "MineGPT",
        icon = painterResource(Res.drawable.img_user_light)
    ) {
        App()
    }
}