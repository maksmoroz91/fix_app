package com.example.vpn_app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OlcrtcRecoveryPolicyTest {
    @Test
    fun frameTooLargeReconnectFailureIsTrackedBeforeRestarting() {
        val line =
            "22:14:05 logger.go:110: handshake on reconnect failed (attempt 1): " +
                "handshake client: read welcome: handshake: frame too large: 2065856101 > 65536"

        assertTrue(OlcrtcRecoveryPolicy.isReconnectHandshakeCorruption(line))
        assertFalse(OlcrtcRecoveryPolicy.shouldRestartNative(line))
        assertNull(OlcrtcRecoveryPolicy.restartReason(line))
    }

    @Test
    fun exhaustedReconnectAttemptsTriggerNativeRestart() {
        val line =
            "22:15:50 logger.go:110: client reconnect: exhausted 5 handshake attempts " +
                "(reason=carrier) - keeping listener up"

        assertTrue(OlcrtcRecoveryPolicy.shouldRestartNative(line))
        assertEquals(
            "reconnect handshake attempts exhausted",
            OlcrtcRecoveryPolicy.restartReason(line)
        )
    }

    @Test
    fun restartReasonUsesNativeRestartDecisionAsGate() {
        val restartLine =
            "22:15:50 logger.go:110: client reconnect: exhausted 5 handshake attempts " +
                "(reason=carrier) - keeping listener up"
        val corruptionLine =
            "22:14:05 logger.go:110: handshake on reconnect failed (attempt 1): " +
                "handshake client: read welcome: handshake: frame too large: 2065856101 > 65536"

        assertTrue(OlcrtcRecoveryPolicy.shouldRestartNative(restartLine))
        assertEquals(
            OlcrtcRecoveryPolicy.shouldRestartNative(restartLine),
            OlcrtcRecoveryPolicy.restartReason(restartLine) != null
        )
        assertFalse(OlcrtcRecoveryPolicy.shouldRestartNative(corruptionLine))
        assertEquals(
            OlcrtcRecoveryPolicy.shouldRestartNative(corruptionLine),
            OlcrtcRecoveryPolicy.restartReason(corruptionLine) != null
        )
    }

    @Test
    fun commonReconnectNoiseDoesNotTriggerNativeRestart() {
        assertFalse(
            OlcrtcRecoveryPolicy.shouldRestartNative(
                "22:11:14 logger.go:110: sid=17 connect failed: sid=17: " +
                    "remote not ready (read_err=timeout ack=[0])"
            )
        )
        assertFalse(
            OlcrtcRecoveryPolicy.shouldRestartNative(
                "22:12:44 logger.go:100: client reconnect reason=liveness - " +
                    "tearing down smux session"
            )
        )
        assertFalse(
            OlcrtcRecoveryPolicy.shouldRestartNative(
                "19:56:55 logger.go:100: SOCKS5 server listening on 127.0.0.1:10808"
            )
        )
    }
}
