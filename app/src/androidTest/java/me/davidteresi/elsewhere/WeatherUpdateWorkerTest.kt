/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import android.util.Log
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.testing.SynchronousExecutor
import java.util.Locale
import kotlin.math.roundToInt

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.CoreMatchers.*
import org.junit.Assert

@RunWith(AndroidJUnit4::class)
class WeatherUpdateWorkerTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = WeatherUpdateDispatcher()

        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun weatherUpdateWorker_succeeds() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val request = OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }
}

@RunWith(AndroidJUnit4::class)
class WeatherUpdateWorkerFailTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        activityTestRule.launchActivity(null)
    }

    @Test
    fun weatherUpdateWorker_failsWithNoInternet() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val request = OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
}

@RunWith(AndroidJUnit4::class)
class NewDayWeatherUpdateWorkerTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = WeatherUpdateDispatcher()

        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val application = context.getApplicationContext() as ElsewhereTestApp
        (application.stateManager as FakeStateManager).newDay = true
        (application.placeDataSource as FakePlaceDataSource).place = test_attrs.updated.place
        
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun weatherUpdateWorker_newDaySucceeds() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val request = OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }
}

@RunWith(AndroidJUnit4::class)
class NewDayWeatherUpdateWorkerFailTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val application = context.getApplicationContext() as ElsewhereTestApp
        (application.stateManager as FakeStateManager).newDay = true
        (application.placeDataSource as FakePlaceDataSource).place = test_attrs.updated.place
        
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        activityTestRule.launchActivity(null)
    }

    @Test
    fun weatherUpdateWorker_newDayFailsWithNoInternet() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val request = OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
}