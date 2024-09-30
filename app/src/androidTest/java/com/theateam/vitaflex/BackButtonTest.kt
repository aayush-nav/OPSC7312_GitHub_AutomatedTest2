package com.theateam.vitaflex

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class BackButtonTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<LoginActivity> = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testBackButtonFunctionality() {
        // Assume user logs in and is redirected to DashboardActivity
        // Simulate pressing the back button
        pressBack()

        // Check if the login screen is displayed after pressing back
        onView(withId(R.id.login_back_btn)).check(matches(isDisplayed()))
    }
}
