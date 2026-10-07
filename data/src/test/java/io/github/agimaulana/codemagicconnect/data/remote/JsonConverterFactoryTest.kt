package io.github.agimaulana.codemagicconnect.data.remote

import io.github.agimaulana.codemagicconnect.data.remote.dto.PageDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.TeamAppDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonConverterFactoryTest {

    private val retrofit = Retrofit.Builder().baseUrl("https://codemagic.io/").build()

    @Test
    fun `given a parameterized dto type when decoding then the generic serializer resolves`() {
        val factory = JsonConverterFactory.create(Json { ignoreUnknownKeys = true })
        val type = object : TypeRef<PageDto<TeamAppDto>>() {}.type
        val converter = checkNotNull(factory.responseBodyConverter(type, emptyArray(), retrofit))
        val body = """{"data":[{"id":"app-1","name":"acme","repository":{"url":"git@github.com:acme/app.git"}}],"page_size":30,"current_page":1,"total_pages":1}"""
            .toResponseBody("application/json".toMediaType())

        val page = converter.convert(body) as PageDto<*>

        assertEquals(1, page.data.size)
    }

    private abstract class TypeRef<T> {
        val type: Type
            get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
    }
}
