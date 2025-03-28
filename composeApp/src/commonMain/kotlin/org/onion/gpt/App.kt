package org.onion.gpt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import minegpt.composeapp.generated.resources.Res
import minegpt.composeapp.generated.resources.compose_multiplatform
import org.onion.gpt.ui.navigation.NavActions
import org.onion.gpt.ui.navigation.destination.RootDestination
import org.onion.gpt.ui.screen.home.HomeScreen
import org.onion.gpt.ui.screen.splash.SplashScreen
import org.onion.gpt.ui.theme.AppTheme
import org.onion.gpt.ui.theme.MediumText

@Composable
@Preview
fun App() {
    AppTheme {
        val rootNavController = rememberNavController()
        val rootNavActions = remember(rootNavController) {
            NavActions(rootNavController)
        }
        NavHost(
            navController = rootNavController,
            startDestination = RootDestination.Splash.route
        ) {

            composable(RootDestination.Splash.route) {
                SplashScreen(
                    startHomeScreen = { rootNavActions.popAndNavigation(RootDestination.Home) }
                )
            }
            composable(RootDestination.Home.route) {
                HomeScreen()
            }
        }
    }
}