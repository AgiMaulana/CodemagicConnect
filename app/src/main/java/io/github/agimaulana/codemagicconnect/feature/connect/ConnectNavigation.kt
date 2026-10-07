package io.github.agimaulana.codemagicconnect.feature.connect

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.github.agimaulana.codemagicconnect.core.navigation.GDDestinationRoute
import io.github.agimaulana.codemagicconnect.core.navigation.GDNavigationRoute

const val CONNECT_ROUTE = "connect_route"

fun NavController.navigateToConnect(navOptions: NavOptions? = null) {
    navigate(
        route = GDNavigationRoute.Builder(CONNECT_ROUTE).build(),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.connectScreen(
    onNavigationEvent: (ConnectViewModel.NavigationEvent) -> Unit
) {
    composable(route = GDDestinationRoute.Builder(CONNECT_ROUTE).build()) {
        ConnectRoute(onNavigationEvent = onNavigationEvent)
    }
}
