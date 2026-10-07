package io.github.agimaulana.codemagicconnect.data.remote.api

import io.github.agimaulana.codemagicconnect.data.remote.dto.BuildDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.OverTheAirUpdateDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.PageDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.SingleDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.TeamAppDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.TeamDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.UserAppDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.WorkflowDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CodemagicApiService {

    @GET("api/v3/user/apps")
    suspend fun getUserAppsWithToken(@Header(API_TOKEN_HEADER) token: String): PageDto<UserAppDto>

    @GET("api/v3/user/teams")
    suspend fun getUserTeams(@Query("page_size") pageSize: Int): PageDto<TeamDto>

    @GET("api/v3/teams/{team_id}/apps")
    suspend fun getTeamApps(
        @Path("team_id") teamId: String,
        @Query("page_size") pageSize: Int
    ): PageDto<TeamAppDto>

    @GET("api/v3/apps/{app_id}/workflows")
    suspend fun getAppWorkflows(@Path("app_id") appId: String): PageDto<WorkflowDto>

    @GET("api/v3/teams/{team_id}/builds")
    suspend fun getTeamBuilds(
        @Path("team_id") teamId: String,
        @Query("app_id") appId: String
    ): PageDto<BuildDto>

    @GET("api/v3/builds/{build_id}")
    suspend fun getBuildDetails(@Path("build_id") buildId: String): SingleDto<BuildDto>

    @GET("api/v3/over-the-air-updates")
    suspend fun getOverTheAirUpdates(): List<OverTheAirUpdateDto>

    companion object {
        const val API_TOKEN_HEADER = "x-auth-token"
    }
}
