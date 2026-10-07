package io.github.agimaulana.codemagicconnect.data.remote

import kotlinx.serialization.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Minimal Retrofit converter backed by kotlinx.serialization so the app does not need an
 * extra converter artifact.
 */
internal class JsonConverterFactory private constructor(
    private val json: Json
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, Any> {
        val serializer = json.serializersModule.serializer(type)
        return Converter { body -> json.decodeFromString(serializer, body.string()) }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<Any, RequestBody> {
        val serializer = json.serializersModule.serializer(type)
        return Converter { value -> json.encodeToString(serializer, value).toRequestBody(JSON_MEDIA_TYPE) }
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()

        fun create(json: Json): Converter.Factory = JsonConverterFactory(json)
    }
}
