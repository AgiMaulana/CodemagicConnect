package io.github.agimaulana.codemagicconnect.feature.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.github.agimaulana.codemagicconnect.core.navigation.GDDestinationRoute
import io.github.agimaulana.codemagicconnect.core.navigation.GDNavigationRoute

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(
        route = GDNavigationRoute.Builder(SETTINGS_ROUTE).build(),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.settingsScreen(
    onNavigationEvent: (SettingsViewModel.NavigationEvent) -> Unit
) {
    composable(route = GDDestinationRoute.Builder(SETTINGS_ROUTE).build()) {
        SettingsRoute(onNavigationEvent = onNavigationEvent)
    }
}
