package com.example.vpn_app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OlcrtcServerConfigExampleTest {
    @Test
    fun serverExampleMatchesAndroidClientDefaults() {
        val yaml = readRepositoryFile("docs/olcrtc/server.yaml.example")
        val trimmedLines = yaml.lineSequence().map { it.trim() }.toSet()

        assertTrue(trimmedLines.contains("mode: srv"))
        assertTrue(trimmedLines.contains("provider: jitsi"))
        assertTrue(trimmedLines.contains("transport: datachannel"))
        assertTrue(trimmedLines.contains("dns: \"8.8.8.8:53\""))
        assertTrue(trimmedLines.contains("proxy_addr: \"127.0.0.1\""))
        assertTrue(trimmedLines.contains("proxy_port: 10808"))
        assertTrue(trimmedLines.contains("interval: 30s"))
        assertTrue(trimmedLines.contains("timeout: 90s"))
        assertTrue(trimmedLines.contains("failures: 3"))
        assertTrue(trimmedLines.contains("data: /opt/olcrtc/data"))
        assertTrue(trimmedLines.contains("debug: false"))
        assertFalse(Regex("""(?m)^\s*ping_interval\s*:""").containsMatchIn(yaml))
    }

    private fun readRepositoryFile(path: String): String {
        val root = findRepositoryRoot()
        val file = File(root, path)
        assertTrue("Missing $path", file.isFile)
        return file.readText()
    }

    private fun findRepositoryRoot(): File {
        var directory = File(System.getProperty("user.dir")).absoluteFile
        repeat(8) {
            if (File(directory, "pubspec.yaml").isFile) {
                return directory
            }
            val parent = directory.parentFile ?: break
            directory = parent
        }

        throw AssertionError("Could not locate repository root from ${System.getProperty("user.dir")}")
    }
}
