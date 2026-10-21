package io.github.agimaulana.codemagicconnect.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApkCompatibilityTest {

    private fun artifact(name: String) = BuildArtifact("a1", name, 1_000_000)

    @Test
    fun `given uppercase apk name when is apk then true`() {
        assertTrue(artifact("APP-RELEASE.APK").isApk())
    }

    @Test
    fun `given aab name when is apk then false`() {
        assertFalse(artifact("app-release.aab").isApk())
    }

    @Test
    fun `given abi specific apk when apk abis then abi is detected`() {
        assertEquals(setOf(ApkAbi.X86_64), artifact("app-x86_64-release.apk").apkAbis())
        assertEquals(setOf(ApkAbi.ARMEABI_V7A), artifact("app-armeabi-v7a-release.apk").apkAbis())
    }

    @Test
    fun `given universal apk when apk abis then no abi is detected`() {
        assertEquals(emptySet<ApkAbi>(), artifact("app-release.apk").apkAbis())
    }

    @Test
    fun `given multi abi apk when apk abis then every abi is detected`() {
        assertEquals(
            setOf(ApkAbi.ARM64_V8A, ApkAbi.X86_64),
            artifact("app-arm64-v8a-x86_64-release.apk").apkAbis()
        )
    }

    @Test
    fun `given matching abi when compatibility checked then apk is compatible`() {
        assertTrue(artifact("app-arm64-v8a-release.apk").isCompatibleApk(listOf("arm64-v8a")))
    }

    @Test
    fun `given non matching abi when compatibility checked then apk is incompatible`() {
        assertFalse(artifact("app-x86_64-release.apk").isCompatibleApk(listOf("arm64-v8a")))
    }

    @Test
    fun `given universal apk when compatibility checked then apk is compatible`() {
        assertTrue(artifact("app-release.apk").isCompatibleApk(listOf("arm64-v8a")))
    }

    @Test
    fun `given multi abi apk when one abi matches then apk is compatible`() {
        assertTrue(artifact("app-arm64-v8a-x86_64-release.apk").isCompatibleApk(listOf("x86_64")))
    }

    @Test
    fun `given aab when compatibility checked then artifact is not compatible`() {
        assertFalse(artifact("app-release.aab").isCompatibleApk(listOf("arm64-v8a")))
    }
}
