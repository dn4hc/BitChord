package com.music.bitchord.data.sources.addon

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Regression coverage for addons that declare capabilities using resources. */
class AddonManifestTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `resource-only manifest is playable without optional endpoint objects`() {
        val manifest = json.decodeFromString<AddonManifest>(
            """{"id":"qobuz","name":"Qobuz","version":"1.0.0","resources":["search","stream"]}"""
        )
        assertTrue(manifest.isPlayable)
    }

    @Test
    fun `declarations tolerate mixed case and surrounding whitespace`() {
        val manifest = AddonManifest(id = "music", resources = listOf(" Search ", "STREAM"))
        assertTrue(manifest.isPlayable)
    }

    @Test
    fun `legacy endpoint-only manifest remains playable`() {
        val manifest = AddonManifest(
            id = "legacy",
            search = AddonSearchEndpoint(),
            stream = AddonStreamEndpoint(),
        )
        assertTrue(manifest.isPlayable)
    }

    @Test
    fun `partial declarations across representations can supply both capabilities`() {
        val manifest = AddonManifest(
            id = "hybrid",
            resources = listOf("search"),
            stream = AddonStreamEndpoint(),
        )
        assertTrue(manifest.isPlayable)
    }

    @Test
    fun `missing streaming capability is rejected`() {
        assertFalse(AddonManifest(id = "search-only", resources = listOf("search")).isPlayable)
    }

    @Test
    fun `missing search capability is rejected`() {
        assertFalse(AddonManifest(id = "stream-only", resources = listOf("stream")).isPlayable)
    }

    @Test
    fun `no capabilities is rejected`() {
        assertFalse(AddonManifest(id = "empty").isPlayable)
    }
    @Test
    fun `official search response maps track metadata and lossless claims`() {
        val response = json.decodeFromString<AddonSearchResponse>(
            """{"tracks":[{"id":"123","title":"Test Song","artist":"Example","duration":242,
               "artworkURL":"https://example.org/art.jpg","format":"flac","audioQuality":"LOSSLESS",
               "sampleRate":96000,"bitrate":2800000}]}"""
        )
        val track = response.tracks.single()
        assertEquals(242, track.effectiveDurationSec)
        assertEquals("https://example.org/art.jpg", track.effectiveArtworkUrl)
        assertEquals("flac", track.effectiveCodec)
        assertEquals(96000, track.effectiveSampleRateHz)
        assertEquals(2800, track.effectiveBitrateKbps)
        assertTrue(track.claimsLossless)
    }

    @Test
    fun `lossy codec overrides a claimed lossless label`() {
        val track = json.decodeFromString<AddonTrack>(
            """{"id":"bad","title":"Lossy","format":"mp3","audioQuality":"LOSSLESS"}"""
        )
        assertFalse(track.claimsLossless)
    }

    @Test
    fun `official stream response maps sample rate and bitrate`() {
        val stream = json.decodeFromString<AddonStream>(
            """{"url":"https://example.org/audio.flac","format":"flac",
                "codec":"flac","sampleRate":96000,"bitDepth":24,"bitrate":2800000,
                "container":"flac","manifest":"none","encrypted":false}"""
        )
        assertEquals("flac", stream.effectiveCodec)
        assertEquals(96000, stream.effectiveSampleRateHz)
        assertEquals(2800, stream.effectiveBitrateKbps)
        assertFalse(stream.encrypted)
    }

}
