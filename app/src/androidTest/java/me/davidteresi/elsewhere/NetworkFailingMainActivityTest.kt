/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

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
class NetworkFailingMainActivityTest {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        activityTestRule.launchActivity(null)
    }

    @Test
    fun placeDisplay_networkFailing() {
        val country = Locale("", test_attrs.place.country)
        val text = "${test_attrs.place.name}, ${country.getDisplayCountry()}"
        
        onView(withId(R.id.place))
            .check(matches(withText(text)))
    }

    @Test
    fun weatherConditionDisplay_networkFailing() {
        val text = "${test_attrs.weather.weather[0].description}"
        
        onView(withId(R.id.condition))
            .check(matches(withText(text)))
    }

    @Test
    fun weatherHumidityDisplay_networkFailing() {
        val text = "${test_attrs.weather.main.humidity.roundToInt()}"
        
        onView(withId(R.id.humidity))
            .check(matches(withText(containsString(text))))
    }
}

@RunWith(AndroidJUnit4::class)
class NetworkFailingMainActivityTestImperial {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        with (prefs!!.edit()) {
            putString(context.getString(R.string.units), "imperial")
            apply()
        }

        activityTestRule.launchActivity(null)
    }

    @Test
    fun imperialTempDisplay_networkFailing() {
        val fahrenheit = test_attrs.weather.main.temp * (9f/5f) - 459.67f
        val text = "${fahrenheit.roundToInt()}"
        
        onView(withId(R.id.temp))
            .check(matches(withText(containsString(text))))
    }

    @Test
    fun imperialWindSpeedDisplay_networkFailing() {
        val mih = test_attrs.weather.wind.speed * 2.237f
        val text = "${mih.roundToInt()}"
        
        onView(withId(R.id.wind))
            .check(matches(withText(containsString(text))))
    }
}

@RunWith(AndroidJUnit4::class)
class NetworkFailingMainActivityTestMetric {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        with (prefs!!.edit()) {
            putString(context.getString(R.string.units), "metric")
            apply()
        }

        activityTestRule.launchActivity(null)
    }

    @Test
    fun metricTempDisplay_networkFailing() {
        val celsius = test_attrs.weather.main.temp - 273
        val text = "${celsius.roundToInt()}"
        
        onView(withId(R.id.temp))
            .check(matches(withText(containsString(text))))
    }

    @Test
    fun metricWindSpeedDisplay_networkFailing() {
        val kmh = test_attrs.weather.wind.speed * 3600 / 1000
        val text = "${kmh.roundToInt()}"
        
        onView(withId(R.id.wind))
            .check(matches(withText(containsString(text))))
    }
}