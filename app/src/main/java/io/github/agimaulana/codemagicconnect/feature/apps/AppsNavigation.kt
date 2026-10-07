package io.github.agimaulana.codemagicconnect.feature.apps

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.github.agimaulana.codemagicconnect.core.navigation.GDDestinationRoute
import io.github.agimaulana.codemagicconnect.core.navigation.GDNavigationRoute

const val APPS_ROUTE = "apps_route"

fun NavController.navigateToApps(navOptions: NavOptions? = null) {
    navigate(
        route = GDNavigationRoute.Builder(APPS_ROUTE).build(),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.appsScreen(
    onNavigationEvent: (AppsViewModel.NavigationEvent) -> Unit
) {
    composable(route = GDDestinationRoute.Builder(APPS_ROUTE).build()) {
        AppsRoute(onNavigationEvent = onNavigationEvent)
    }
}
