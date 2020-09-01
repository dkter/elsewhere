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
class NewDayFailingMainActivityTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val application = context.getApplicationContext() as ElsewhereTestApp
        (application.stateManager as FakeStateManager).newDay = true
        (application.placeDataSource as FakePlaceDataSource).place = test_attrs.updated.place

        activityTestRule.launchActivity(null)
    }

    @Test
    fun placeDisplay_newDayDontUpdate() {
        val country = Locale("", test_attrs.place.country)
        val text = "${test_attrs.place.name}, ${country.getDisplayCountry()}"
        
        onView(withId(R.id.place))
            .check(matches(withText(text)))
    }
}

@RunWith(AndroidJUnit4::class)
class NewDayUpdatingMainActivityTest {
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

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun placeDisplay_newDayUpdate() {
        val country = Locale("", test_attrs.updated.place.country)
        val text = "${test_attrs.updated.place.name}, ${country.getDisplayCountry()}"
        
        onView(withId(R.id.place))
            .check(matches(withText(text)))
    }
}