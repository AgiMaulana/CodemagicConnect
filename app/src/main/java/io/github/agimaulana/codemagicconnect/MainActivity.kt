package io.github.agimaulana.codemagicconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.agimaulana.codemagicconnect.feature.apps.AppsViewModel
import io.github.agimaulana.codemagicconnect.feature.apps.appsScreen
import io.github.agimaulana.codemagicconnect.feature.apps.navigateToApps
import io.github.agimaulana.codemagicconnect.feature.builds.BuildsViewModel
import io.github.agimaulana.codemagicconnect.feature.builds.buildsScreen
import io.github.agimaulana.codemagicconnect.feature.builds.navigateToBuilds
import io.github.agimaulana.codemagicconnect.feature.connect.CONNECT_ROUTE
import io.github.agimaulana.codemagicconnect.feature.connect.ConnectViewModel
import io.github.agimaulana.codemagicconnect.feature.connect.connectScreen
import io.github.agimaulana.codemagicconnect.feature.connect.navigateToConnect
import io.github.agimaulana.codemagicconnect.feature.settings.SettingsViewModel
import io.github.agimaulana.codemagicconnect.feature.settings.navigateToSettings
import io.github.agimaulana.codemagicconnect.feature.settings.settingsScreen
import io.github.agimaulana.codemagicconnect.ui.theme.CodemagicConnectTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            CodemagicConnectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = CONNECT_ROUTE // TODO: logic to select start destination
                    ) {
                        connectScreen(onNavigationEvent = { event ->
                            when (event) {
                                ConnectViewModel.NavigationEvent.NavigateToApps -> {
                                    navController.navigateToApps(
                                        navOptions = NavOptions.Builder().setPopUpTo(CONNECT_ROUTE, inclusive = true).build()
                                    )
                                }
                                is ConnectViewModel.NavigationEvent.NavigateToBuilds -> {
                                    navController.navigateToBuilds(
                                        appId = event.appId,
                                        navOptions = NavOptions.Builder().setPopUpTo(CONNECT_ROUTE, inclusive = true).build()
                                    )
                                }
                                ConnectViewModel.NavigationEvent.NavigateBack -> {
                                    navController.popBackStack()
                                }
                                is ConnectViewModel.NavigationEvent.OpenBrowser -> {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.url)))
                                }
                            }
                        })
                        appsScreen(onNavigationEvent = { event ->
                            when (event) {
                                is AppsViewModel.NavigationEvent.NavigateToBuilds -> {
                                    navController.navigateToBuilds(event.appId)
                                }
                            }
                        })
                        buildsScreen(onNavigationEvent = { event ->
                            when (event) {
                                BuildsViewModel.NavigationEvent.NavigateBack -> navController.popBackStack()
                                BuildsViewModel.NavigationEvent.NavigateToSettings -> navController.navigateToSettings()
                                BuildsViewModel.NavigationEvent.NavigateToApps -> navController.navigateToApps()
                            }
                        })
                        settingsScreen(onNavigationEvent = { event ->
                            when (event) {
                                SettingsViewModel.NavigationEvent.NavigateBack -> navController.popBackStack()
                                is SettingsViewModel.NavigationEvent.NavigateToConnect -> {
                                    val navOptions = if (event.suppressAutoRedirect) {
                                        NavOptions.Builder().build()
                                    } else {
                                        NavOptions.Builder().setPopUpTo(0, inclusive = true).build()
                                    }
                                    navController.navigateToConnect(
                                        suppressAutoRedirect = event.suppressAutoRedirect,
                                        navOptions = navOptions
                                    )
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}
