package bannerga.com.checkmytrain.view.tests

import androidx.test.rule.ActivityTestRule

import org.junit.Rule
import org.junit.Test

import bannerga.com.checkmytrain.view.activity.journey.JourneyActivity
import bannerga.com.checkmytrain.view.robot.JourneyRobot

class AddJourneyTest {

    @Rule
    @JvmField
    var journeyActivity = ActivityTestRule(JourneyActivity::class.java)

    @Test
    fun testCanAddJourney() {
        val departing = "King's Park"
        val arriving = "Glasgow Central"
        val hour = 5
        val minutes = 35
        JourneyRobot()
                .addJourney(departing, arriving, hour, minutes)
                .viewSavedCards()
                .checkCardIsDisplayed(departing, arriving)
    }

}

