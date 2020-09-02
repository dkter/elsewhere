/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import java.util.Locale
import kotlin.math.roundToInt

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class NoWikipediaMainActivityTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val application = context.getApplicationContext() as ElsewhereTestApp
        (application.stateManager as FakeStateManager).wpTitle = null

        activityTestRule.launchActivity(null)
    }

    @Test
    fun wikipediaChip_noWikipedia_dontShow() {
        onView(withId(R.id.wikipedia_chip))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}

@RunWith(AndroidJUnit4::class)
class NoWikipediaUpdatingMainActivityTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = NoWikipediaDispatcher()

        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val application = context.getApplicationContext() as ElsewhereTestApp
        (application.stateManager as FakeStateManager).wpTitle = null

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun wikipediaChip_noWikipediaUpdating_dontShow() {
        onView(withId(R.id.wikipedia_chip))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}