package io.github.agimaulana.codemagicconnect.data.remote.mapper

import io.github.agimaulana.codemagicconnect.data.remote.dto.BuildDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.PageDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.SingleDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.TeamAppDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.TeamDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.WorkflowDto
import io.github.agimaulana.codemagicconnect.domain.model.BuildStatus
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DtoMappersTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `given team apps json when parsed then maps id name repository and owning team`() {
        val payload = """
            {"data":[{"id":"app-1","name":"acme","repository":{"url":"git@github.com:acme/app.git"},"last_build_id":null,"archived":false}],"page_size":30,"current_page":1,"total_pages":1}
        """.trimIndent()

        val application = json.decodeFromString<PageDto<TeamAppDto>>(payload).data.single().toDomain("team-9")

        assertEquals("app-1", application.id)
        assertEquals("acme", application.name)
        assertEquals("git@github.com:acme/app.git", application.repositoryUrl)
        assertEquals("team-9", application.teamId)
        assertNull(application.lastBuildTime)
    }

    @Test
    fun `given teams json when parsed then maps id and name`() {
        val payload = """{"data":[{"id":"team-9","name":"Personal account"}],"page_size":30,"current_page":1,"total_pages":1}"""

        val team = json.decodeFromString<PageDto<TeamDto>>(payload).data.single()

        assertEquals("team-9", team.id)
        assertEquals("Personal account", team.name)
    }

    @Test
    fun `given workflows json when parsed then maps to domain workflows`() {
        val payload = """{"data":[{"id":"w1","source":{"type":"yaml"},"name":"android-release"}]}"""

        val workflow = json.decodeFromString<PageDto<WorkflowDto>>(payload).data.single().toDomain("app-1")

        assertEquals("w1", workflow.id)
        assertEquals("android-release", workflow.name)
        assertEquals("app-1", workflow.appId)
    }

    @Test
    fun `given null workflow name when parsed then maps to empty name`() {
        val payload = """{"data":[{"id":"w1","source":{"type":"yaml"},"name":null}]}"""

        val workflow = json.decodeFromString<PageDto<WorkflowDto>>(payload).data.single().toDomain("app-1")

        assertEquals("", workflow.name)
    }

    @Test
    fun `given builds json when parsed then maps nested workflow status and artifacts`() {
        val payload = """
            {"data":[{"id":"b1","app_id":"app-1","workflow":{"id":"w1","name":"android"},"status":"finished","branch":"main","started_at":"2026-10-01T10:00:00Z","finished_at":"2026-10-01T10:10:00Z","artifacts":[{"name":"app-release.apk","size_in_bytes":12345678,"short_lived_download_url":"https://codemagic.io/artifacts/b1/app.apk","type":"apk"}]}],"page_size":30,"cursor":null}
        """.trimIndent()

        val build = json.decodeFromString<PageDto<BuildDto>>(payload).data.single().toDomain()

        assertEquals("b1", build.id)
        assertEquals("app-1", build.appId)
        assertEquals("w1", build.workflowId)
        assertEquals(BuildStatus.FINISHED, build.status)
        assertEquals("main", build.branch)
        assertEquals("2026-10-01T10:00:00Z", build.startedAt)

        val artifact = build.artifacts.single()
        assertEquals("b1#0", artifact.id)
        assertEquals("app-release.apk", artifact.name)
        assertEquals(12_345_678L, artifact.sizeBytes)
        assertEquals("https://codemagic.io/artifacts/b1/app.apk", artifact.downloadUrl)
    }

    @Test
    fun `given build details json when parsed then unwraps the data object`() {
        val payload = """{"data":{"id":"b1","app_id":"app-1","workflow":{"id":"w1"},"status":"failed","artifacts":[]}}"""

        val build = json.decodeFromString<SingleDto<BuildDto>>(payload).data.toDomain()

        assertEquals("b1", build.id)
        assertEquals(BuildStatus.FAILED, build.status)
    }

    @Test
    fun `given statuses when mapped then known values resolve and unknown falls back to queued`() {
        assertEquals(BuildStatus.FINISHED, "finished".toBuildStatus())
        assertEquals(BuildStatus.FAILED, "failed".toBuildStatus())
        assertEquals(BuildStatus.FAILED, "timeout".toBuildStatus())
        assertEquals(BuildStatus.CANCELED, "skipped".toBuildStatus())
        assertEquals(BuildStatus.BUILDING, "publishing".toBuildStatus())
        assertEquals(BuildStatus.BUILDING, "preparing".toBuildStatus())
        assertEquals(BuildStatus.QUEUED, "queued".toBuildStatus())
        assertEquals(BuildStatus.QUEUED, "something-new".toBuildStatus())
    }
}
