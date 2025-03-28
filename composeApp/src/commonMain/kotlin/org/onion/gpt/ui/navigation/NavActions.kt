/*
 * Copyright 2024 The ZZZ Archive Open Source Project by mrfatworm
 * License: MIT License
 */

package org.onion.gpt.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import org.onion.gpt.ui.navigation.destination.RootDestination
import org.onion.gpt.ui.navigation.destination.Screen

class NavActions(private val navController: NavHostController) {



    fun navigationToRoute(route: String) {
        navController.navigate(route)
    }

    fun back() {
        navController.popBackStack()
    }


    fun popAndNavigation(destination: Screen) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().route ?: RootDestination.Home.route) {
                this.inclusive = true
            }
        }
    }
}


