package me.davidteresi.elsewhere

import org.junit.Test

import org.junit.Assert.*

class MainActivityKtTest {

    @Test
    fun forceHttps_httpToHttps() {
        assertEquals(
            "https://example.com",
            forceHttps("http://example.com")
        )
    }

    @Test
    fun forceHttps_httpsToHttps() {
        assertEquals(
            "https://example.com",
            forceHttps("https://example.com")
        )
    }
}