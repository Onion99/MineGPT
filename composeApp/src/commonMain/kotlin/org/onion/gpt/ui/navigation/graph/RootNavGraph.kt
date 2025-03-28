/*
 * Copyright 2024 The ZZZ Archive Open Source Project by mrfatworm
 * License: MIT License
 */

package org.onion.gpt.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.onion.gpt.ui.navigation.NavActions
import org.onion.gpt.ui.navigation.destination.RootDestination

@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    rootNavActions: NavActions
) {
    NavHost(
        modifier = modifier,
        navController = rootNavController,
        startDestination = RootDestination.Splash.route
    ) {

    }
}
