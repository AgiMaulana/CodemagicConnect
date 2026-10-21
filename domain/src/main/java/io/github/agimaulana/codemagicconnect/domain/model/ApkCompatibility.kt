package io.github.agimaulana.codemagicconnect.domain.model

/**
 * Artifact names are the only ABI signal available before download, so compatibility is
 * inferred from known ABI markers in the file name. An APK without a marker is treated as
 * universal and therefore compatible.
 */
fun BuildArtifact.isApk(): Boolean = name.endsWith(APK_FILE_EXTENSION, ignoreCase = true)

fun BuildArtifact.apkAbis(): Set<ApkAbi> = ABI_PATTERN.findAll(name)
    .mapNotNull { match -> ApkAbi.entries.firstOrNull { it.marker.equals(match.value, ignoreCase = true) } }
    .toSet()

fun BuildArtifact.isCompatibleApk(deviceAbis: Collection<String>): Boolean {
    if (!isApk()) return false
    val artifactAbis = apkAbis()
    if (artifactAbis.isEmpty()) return true
    val normalizedDeviceAbis = deviceAbis.map { it.lowercase() }.toSet()
    return artifactAbis.any { it.marker in normalizedDeviceAbis }
}

enum class ApkAbi(val marker: String) {
    ARM64_V8A("arm64-v8a"),
    ARMEABI_V7A("armeabi-v7a"),
    ARMEABI("armeabi"),
    X86_64("x86_64"),
    X86("x86"),
    MIPS64("mips64"),
    MIPS("mips"),
    RISCV64("riscv64")
}

private const val APK_FILE_EXTENSION = ".apk"

private val ABI_PATTERN = Regex(
    pattern = ApkAbi.entries
        .sortedByDescending { it.marker.length }
        .joinToString("|") { Regex.escape(it.marker) },
    option = RegexOption.IGNORE_CASE
)
