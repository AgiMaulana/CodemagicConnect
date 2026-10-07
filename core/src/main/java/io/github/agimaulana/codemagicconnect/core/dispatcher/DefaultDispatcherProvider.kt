package io.github.agimaulana.codemagicconnect.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

internal class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {

    override fun io(): CoroutineDispatcher = Dispatchers.IO
}
