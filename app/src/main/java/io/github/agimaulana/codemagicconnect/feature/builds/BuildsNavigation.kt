package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.agimaulana.codemagicconnect.core.navigation.GDDestinationRoute
import io.github.agimaulana.codemagicconnect.core.navigation.GDNavigationRoute

const val BUILDS_ROUTE = "builds_route"
const val APP_ID_ARG = "app_id"

fun NavController.navigateToBuilds(appId: String, navOptions: NavOptions? = null) {
    navigate(
        route = GDNavigationRoute.Builder(BUILDS_ROUTE)
            .addPath(appId)
            .build(),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.buildsScreen(
    onNavigationEvent: (BuildsViewModel.NavigationEvent) -> Unit
) {
    composable(
        route = GDDestinationRoute.Builder(BUILDS_ROUTE)
            .addPath(APP_ID_ARG)
            .build(),
        arguments = listOf(
            navArgument(APP_ID_ARG) { type = NavType.StringType }
        )
    ) {
        BuildsRoute(onNavigationEvent = onNavigationEvent)
    }
}
