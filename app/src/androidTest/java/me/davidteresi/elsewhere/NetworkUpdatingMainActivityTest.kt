package me.davidteresi.elsewhere

import androidx.preference.PreferenceManager
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
class NetworkUpdatingMainActivityTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = WeatherUpdateDispatcher()

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun placeDisplay_networkUpdating() {
        val country = Locale("", test_attrs.place.country)
        val text = "${test_attrs.place.name}, ${country.getDisplayCountry()}"
        
        onView(withId(R.id.place))
            .check(matches(withText(text)))
    }

    @Test
    fun weatherConditionDisplay_networkUpdating() {
        val text = "${test_attrs.updated.weather.weather[0].description}"
        
        onView(withId(R.id.condition))
            .check(matches(withText(text)))
    }

    @Test
    fun weatherHumidityDisplay_networkUpdating() {
        val text = "${test_attrs.updated.weather.main.humidity.roundToInt()}"
        
        onView(withId(R.id.humidity))
            .check(matches(withText(containsString(text))))
    }
}

@RunWith(AndroidJUnit4::class)
class NetworkUpdatingMainActivityTestImperial {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = WeatherUpdateDispatcher()

        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        with (prefs!!.edit()) {
            putString(context.getString(R.string.units), "imperial")
            apply()
        }

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun imperialTempDisplay_networkUpdating() {
        val fahrenheit = test_attrs.updated.weather.main.temp * (9f/5f) - 459.67f
        val text = "${fahrenheit.roundToInt()}"
        
        onView(withId(R.id.temp))
            .check(matches(withText(containsString(text))))
    }

    @Test
    fun imperialWindSpeedDisplay_networkUpdating() {
        val mih = test_attrs.updated.weather.wind.speed * 2.237f
        val text = "${mih.roundToInt()}"
        
        onView(withId(R.id.wind))
            .check(matches(withText(containsString(text))))
    }
}

@RunWith(AndroidJUnit4::class)
class NetworkUpdatingMainActivityTestMetric {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var mockWebServer = okhttp3.mockwebserver.MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = WeatherUpdateDispatcher()

        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        with (prefs!!.edit()) {
            putString(context.getString(R.string.units), "metric")
            apply()
        }

        activityTestRule.launchActivity(null)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun metricTempDisplay_networkUpdating() {
        val celsius = test_attrs.updated.weather.main.temp - 273
        val text = "${celsius.roundToInt()}"
        
        onView(withId(R.id.temp))
            .check(matches(withText(containsString(text))))
    }

    @Test
    fun metricWindSpeedDisplay_networkUpdating() {
        val kmh = test_attrs.updated.weather.wind.speed * 3600 / 1000
        val text = "${kmh.roundToInt()}"
        
        onView(withId(R.id.wind))
            .check(matches(withText(containsString(text))))
    }
}