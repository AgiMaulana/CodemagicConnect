package io.github.agimaulana.codemagicconnect.data.download

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtifactDownloaderTest {

    @Test
    fun `given artifact when resolving name then it uses the artifact name and stays bounded`() {
        val name = ArtifactDownloader.resolveFileName("60a0b1c2d3e4f56789abcdef#0", "app-release.apk")

        assertTrue("expected the artifact name in $name", name.contains("app-release"))
        assertTrue("expected the apk extension in $name", name.endsWith(".apk"))
        assertTrue("expected a bounded name but was ${name.length}", name.length <= 120)
        assertFalse(name.startsWith("."))
    }

    @Test
    fun `given a very long artifact name when resolving then the name is truncated but keeps the extension`() {
        val name = ArtifactDownloader.resolveFileName("b1#0", "x".repeat(400) + ".apk")

        assertTrue(name.length <= 120)
        assertTrue(name.endsWith(".apk"))
    }

    @Test
    fun `given unsafe characters when resolving then they are replaced`() {
        val name = ArtifactDownloader.resolveFileName("b1#0", "my app/../../evil?.apk")

        assertFalse(name.contains('/'))
        assertFalse(name.contains('?'))
        assertFalse(name.contains(' '))
        assertTrue(name.endsWith(".apk"))
    }

    @Test
    fun `given blank artifact name when resolving then the fallback is used`() {
        val name = ArtifactDownloader.resolveFileName("b1#0", "")

        assertEquals("b1_0-artifact", name)
    }
}
