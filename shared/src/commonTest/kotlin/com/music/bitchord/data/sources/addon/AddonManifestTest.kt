package com.music.bitchord.data.sources.addon

import kotlinx.serialization.json.Json
import kotlin.test.Test
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
}
