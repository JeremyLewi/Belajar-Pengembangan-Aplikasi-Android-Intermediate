package com.example.storyapp.view.login

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.storyapp.R
import com.example.storyapp.view.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_loginLoadStoryAndLogout() {
        val validEmail = "lewi@gmail.com"
        val validPassword = "lewi12345"
        Intents.init()

        // login
        onView(withId(R.id.emailEditText)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(validPassword), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // load story
        onView(withId(R.id.rvStory)).check(matches(isDisplayed()))
        onView(withId(R.id.rvStory)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                10
            )
        )

        //logout
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.logout)).perform(click())

        // check login page
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()))

    }
}
