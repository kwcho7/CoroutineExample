package com.cools.coroutineexample.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.cools.coroutineexample.MainActivity
import com.cools.coroutineexample.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    var activityRule = activityScenarioRule<MainActivity>()


    @Before
    fun setup() {
    }

    @Test
    fun refreshTest() = runBlocking{
        delay(1000)
        onView(withId(R.id.refreshButton)).perform(ViewActions.click())
        delay(4000)
    }
}