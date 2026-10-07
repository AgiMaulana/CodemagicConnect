package io.github.agimaulana.codemagicconnect.feature.connect

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.agimaulana.codemagicconnect.core.navigation.GDDestinationRoute
import io.github.agimaulana.codemagicconnect.core.navigation.GDNavigationRoute

const val CONNECT_ROUTE = "connect_route"
const val CONNECT_SUPPRESS_AUTO_REDIRECT_ARG = "suppress_auto_redirect"

fun NavController.navigateToConnect(
    suppressAutoRedirect: Boolean = false,
    navOptions: NavOptions? = null
) {
    val route = if (suppressAutoRedirect) {
        GDNavigationRoute.Builder(CONNECT_ROUTE)
            .addPath(suppressAutoRedirect.toString())
            .build()
    } else {
        GDNavigationRoute.Builder(CONNECT_ROUTE).build()
    }
    navigate(route = route, navOptions = navOptions)
}

fun NavGraphBuilder.connectScreen(
    onNavigationEvent: (ConnectViewModel.NavigationEvent) -> Unit
) {
    composable(route = GDDestinationRoute.Builder(CONNECT_ROUTE).build()) {
        ConnectRoute(onNavigationEvent = onNavigationEvent)
    }
    composable(
        route = GDDestinationRoute.Builder(CONNECT_ROUTE)
            .addPath(CONNECT_SUPPRESS_AUTO_REDIRECT_ARG)
            .build(),
        arguments = listOf(
            navArgument(CONNECT_SUPPRESS_AUTO_REDIRECT_ARG) { type = NavType.BoolType }
        )
    ) {
        ConnectRoute(onNavigationEvent = onNavigationEvent)
    }
}
