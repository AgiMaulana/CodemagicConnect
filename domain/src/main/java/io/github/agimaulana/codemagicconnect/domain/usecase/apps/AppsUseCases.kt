package io.github.agimaulana.codemagicconnect.domain.usecase.apps

import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetApplicationsUseCase {
    suspend operator fun invoke(): List<CodemagicApplication>
}

interface ObserveAppPreferencesUseCase {
    operator fun invoke(): Flow<AppPreferences>
}

interface ObserveFavoriteApplicationsUseCase {
    operator fun invoke(): Flow<Set<String>>
}

interface SetDefaultApplicationUseCase {
    suspend operator fun invoke(app: CodemagicApplication)
}

interface ToggleFavoriteApplicationUseCase {
    suspend operator fun invoke(appId: String)
}

interface SetOpenAppAutomaticallyUseCase {
    suspend operator fun invoke(enabled: Boolean)
}

internal class GetApplicationsUseCaseImpl @Inject constructor(
    private val applicationsGateway: ApplicationsGateway
) : GetApplicationsUseCase {

    override suspend fun invoke(): List<CodemagicApplication> = applicationsGateway.getApplications()
}

internal class ObserveAppPreferencesUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : ObserveAppPreferencesUseCase {

    override fun invoke(): Flow<AppPreferences> = preferencesGateway.observePreferences()
}

internal class ObserveFavoriteApplicationsUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : ObserveFavoriteApplicationsUseCase {

    override fun invoke(): Flow<Set<String>> = preferencesGateway.observeFavoriteIds()
}

internal class SetDefaultApplicationUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : SetDefaultApplicationUseCase {

    override suspend fun invoke(app: CodemagicApplication) {
        preferencesGateway.setDefaultApplication(app.id, app.name)
    }
}

internal class ToggleFavoriteApplicationUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : ToggleFavoriteApplicationUseCase {

    override suspend fun invoke(appId: String) {
        preferencesGateway.toggleFavorite(appId)
    }
}

internal class SetOpenAppAutomaticallyUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : SetOpenAppAutomaticallyUseCase {

    override suspend fun invoke(enabled: Boolean) {
        preferencesGateway.setOpenAppAutomatically(enabled)
    }
}
