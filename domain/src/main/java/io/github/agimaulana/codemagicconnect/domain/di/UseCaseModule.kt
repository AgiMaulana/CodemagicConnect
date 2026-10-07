package io.github.agimaulana.codemagicconnect.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.GetApplicationsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.GetApplicationsUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveAppPreferencesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveAppPreferencesUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveFavoriteApplicationsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveFavoriteApplicationsUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetDefaultApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetDefaultApplicationUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetOpenAppAutomaticallyUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetOpenAppAutomaticallyUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ToggleFavoriteApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ToggleFavoriteApplicationUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.DownloadArtifactUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.DownloadArtifactUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetApplicationUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetBuildsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetBuildsUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.InstallArtifactUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.InstallArtifactUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.ObserveArtifactDownloadsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.ObserveArtifactDownloadsUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.IsTokenStoredUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.IsTokenStoredUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.VerifyAndSaveTokenUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.VerifyAndSaveTokenUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.ota.GetOverTheAirUpdatesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.ota.GetOverTheAirUpdatesUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDefaultApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDefaultApplicationUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDownloadedFilesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDownloadedFilesUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ObserveSettingsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ObserveSettingsUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.RemoveTokenUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.RemoveTokenUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetDeleteApkAfterInstallUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetDeleteApkAfterInstallUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetWifiOnlyUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetWifiOnlyUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.TestConnectionUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.TestConnectionUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.usecase.workflows.GetWorkflowsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.workflows.GetWorkflowsUseCaseImpl
import io.github.agimaulana.codemagicconnect.domain.policy.WifiOnlyDownloadPolicy
import io.github.agimaulana.codemagicconnect.domain.policy.WifiOnlyDownloadPolicyImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UseCaseModule {

    @Binds
    @Singleton
    internal abstract fun bindVerifyAndSaveTokenUseCase(impl: VerifyAndSaveTokenUseCaseImpl): VerifyAndSaveTokenUseCase

    @Binds
    @Singleton
    internal abstract fun bindIsTokenStoredUseCase(impl: IsTokenStoredUseCaseImpl): IsTokenStoredUseCase

    @Binds
    @Singleton
    internal abstract fun bindGetApplicationsUseCase(impl: GetApplicationsUseCaseImpl): GetApplicationsUseCase

    @Binds
    @Singleton
    internal abstract fun bindObserveAppPreferencesUseCase(impl: ObserveAppPreferencesUseCaseImpl): ObserveAppPreferencesUseCase

    @Binds
    @Singleton
    internal abstract fun bindObserveFavoriteApplicationsUseCase(
        impl: ObserveFavoriteApplicationsUseCaseImpl
    ): ObserveFavoriteApplicationsUseCase

    @Binds
    @Singleton
    internal abstract fun bindSetDefaultApplicationUseCase(
        impl: SetDefaultApplicationUseCaseImpl
    ): SetDefaultApplicationUseCase

    @Binds
    @Singleton
    internal abstract fun bindToggleFavoriteApplicationUseCase(
        impl: ToggleFavoriteApplicationUseCaseImpl
    ): ToggleFavoriteApplicationUseCase

    @Binds
    @Singleton
    internal abstract fun bindSetOpenAppAutomaticallyUseCase(
        impl: SetOpenAppAutomaticallyUseCaseImpl
    ): SetOpenAppAutomaticallyUseCase

    @Binds
    @Singleton
    internal abstract fun bindGetApplicationUseCase(impl: GetApplicationUseCaseImpl): GetApplicationUseCase

    @Binds
    @Singleton
    internal abstract fun bindGetBuildsUseCase(impl: GetBuildsUseCaseImpl): GetBuildsUseCase

    @Binds
    @Singleton
    internal abstract fun bindDownloadArtifactUseCase(impl: DownloadArtifactUseCaseImpl): DownloadArtifactUseCase

    @Binds
    @Singleton
    internal abstract fun bindWifiOnlyDownloadPolicy(impl: WifiOnlyDownloadPolicyImpl): WifiOnlyDownloadPolicy

    @Binds
    @Singleton
    internal abstract fun bindObserveArtifactDownloadsUseCase(
        impl: ObserveArtifactDownloadsUseCaseImpl
    ): ObserveArtifactDownloadsUseCase

    @Binds
    @Singleton
    internal abstract fun bindInstallArtifactUseCase(impl: InstallArtifactUseCaseImpl): InstallArtifactUseCase

    @Binds
    @Singleton
    internal abstract fun bindObserveSettingsUseCase(impl: ObserveSettingsUseCaseImpl): ObserveSettingsUseCase

    @Binds
    @Singleton
    internal abstract fun bindTestConnectionUseCase(impl: TestConnectionUseCaseImpl): TestConnectionUseCase

    @Binds
    @Singleton
    internal abstract fun bindRemoveTokenUseCase(impl: RemoveTokenUseCaseImpl): RemoveTokenUseCase

    @Binds
    @Singleton
    internal abstract fun bindClearDefaultApplicationUseCase(
        impl: ClearDefaultApplicationUseCaseImpl
    ): ClearDefaultApplicationUseCase

    @Binds
    @Singleton
    internal abstract fun bindSetWifiOnlyUseCase(impl: SetWifiOnlyUseCaseImpl): SetWifiOnlyUseCase

    @Binds
    @Singleton
    internal abstract fun bindSetDeleteApkAfterInstallUseCase(
        impl: SetDeleteApkAfterInstallUseCaseImpl
    ): SetDeleteApkAfterInstallUseCase

    @Binds
    @Singleton
    internal abstract fun bindClearDownloadedFilesUseCase(
        impl: ClearDownloadedFilesUseCaseImpl
    ): ClearDownloadedFilesUseCase

    @Binds
    @Singleton
    internal abstract fun bindGetWorkflowsUseCase(impl: GetWorkflowsUseCaseImpl): GetWorkflowsUseCase

    @Binds
    @Singleton
    internal abstract fun bindGetOverTheAirUpdatesUseCase(
        impl: GetOverTheAirUpdatesUseCaseImpl
    ): GetOverTheAirUpdatesUseCase
}
