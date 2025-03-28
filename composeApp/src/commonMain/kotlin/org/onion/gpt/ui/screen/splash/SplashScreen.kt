package org.onion.gpt.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import minegpt.composeapp.generated.resources.Res
import minegpt.composeapp.generated.resources.splash_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(startHomeScreen: () -> Unit){

    LaunchedEffect(true) {
        delay(1000)
        startHomeScreen()
    }
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF000223), Color(0xFF0C0934)))),) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.splash_logo),
            contentScale = ContentScale.Inside,
            contentDescription = null,
        )
    }
}