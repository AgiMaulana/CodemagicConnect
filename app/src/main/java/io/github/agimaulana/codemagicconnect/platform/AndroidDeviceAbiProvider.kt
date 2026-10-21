package io.github.agimaulana.codemagicconnect.platform

import android.os.Build
import javax.inject.Inject

internal class AndroidDeviceAbiProvider @Inject constructor() : DeviceAbiProvider {
    override val supportedAbis: List<String>
        get() = Build.SUPPORTED_ABIS.toList()
}
