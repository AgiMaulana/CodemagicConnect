package io.github.agimaulana.codemagicconnect.domain.usecase.builds

import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.ArtifactsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.BuildsGateway
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.BuildStatus
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BuildsUseCasesTest {

    private val applicationsGateway = mockk<ApplicationsGateway>()
    private val buildsGateway = mockk<BuildsGateway>()
    private val artifactsGateway = mockk<ArtifactsGateway>()

    private val app = CodemagicApplication("app-1", "acme", "github.com/acme", null, teamId = "team-9")

    private fun build(id: String, appId: String) = CodemagicBuild(
        id = id,
        appId = appId,
        workflowId = "wf",
        branch = "main",
        status = BuildStatus.FINISHED,
        startedAt = "Today",
        finishedAt = null,
        artifacts = listOf(BuildArtifact("$id#0", "app-release.apk", 100)),
        triggerer = ""
    )

    @Test
    fun `given app id when get application then returns matching application`() = runTest {
        coEvery { applicationsGateway.getApplications() } returns listOf(app)

        assertEquals(app, GetApplicationUseCaseImpl(applicationsGateway).invoke("app-1"))
    }

    @Test
    fun `given unknown app id when get application then null`() = runTest {
        coEvery { applicationsGateway.getApplications() } returns listOf(app)

        assertNull(GetApplicationUseCaseImpl(applicationsGateway).invoke("missing"))
    }

    @Test
    fun `given app when get builds then queries the owning team for that app`() = runTest {
        coEvery { applicationsGateway.getApplications() } returns listOf(app)
        val teamBuild = build("b1", "app-1")
        coEvery { buildsGateway.getTeamBuilds("team-9", "app-1") } returns listOf(teamBuild)

        val result = GetBuildsUseCaseImpl(applicationsGateway, buildsGateway).invoke("app-1")

        assertEquals(listOf(teamBuild), result)
        coVerify(exactly = 1) { buildsGateway.getTeamBuilds("team-9", "app-1") }
    }

    @Test
    fun `given unknown app when get builds then empty without a team call`() = runTest {
        coEvery { applicationsGateway.getApplications() } returns listOf(app)

        val result = GetBuildsUseCaseImpl(applicationsGateway, buildsGateway).invoke("missing")

        assertEquals(emptyList<CodemagicBuild>(), result)
        coVerify(exactly = 0) { buildsGateway.getTeamBuilds(any(), any()) }
    }

    @Test
    fun `given artifact when download then delegates to artifacts gateway`() = runTest {
        val artifact = BuildArtifact("a1", "app.apk", 100)
        coEvery { artifactsGateway.download("b1", artifact) } returns Unit

        DownloadArtifactUseCaseImpl(artifactsGateway).invoke("b1", artifact)

        coVerify(exactly = 1) { artifactsGateway.download("b1", artifact) }
    }

    @Test
    fun `given artifact id when install then delegates to artifacts gateway`() = runTest {
        coEvery { artifactsGateway.install("a1") } returns Unit

        InstallArtifactUseCaseImpl(artifactsGateway).invoke("a1")

        coVerify(exactly = 1) { artifactsGateway.install("a1") }
    }
}
