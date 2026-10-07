package io.github.agimaulana.codemagicconnect.data.di

import javax.inject.Qualifier

/**
 * Marks the OkHttp client used for streaming artifact downloads. It deliberately omits the
 * body-logging interceptor: logging a body buffers the whole response in memory, which is fatal
 * for large APKs and prevents incremental progress reporting.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadClient
