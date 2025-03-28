package org.onion.gpt.ui.navigation.destination


sealed interface RootDestination {
    data object Splash : Screen(route = "splash")
    data object Home : Screen(route = "home")
}