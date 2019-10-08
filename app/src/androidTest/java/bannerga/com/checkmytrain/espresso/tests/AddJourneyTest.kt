package bannerga.com.checkmytrain.espresso.tests

import androidx.test.rule.ActivityTestRule
import bannerga.com.checkmytrain.espresso.robot.JourneyRobot
import bannerga.com.checkmytrain.view.activity.journey.JourneyActivity
import org.junit.Rule
import org.junit.Test

class AddJourneyTest {

    @Rule
    @JvmField
    var journeyActivity = ActivityTestRule(JourneyActivity::class.java)

    @Test
    fun testCanAddJourney() {
        val departing = "Kings Park"
        val arriving = "Glasgow Central"
        val hour = 5
        val minutes = 35
        JourneyRobot()
                .addJourney(departing, arriving, hour, minutes)
                .viewSavedCards()
                .checkCardIsDisplayed(departing, arriving)
    }

}

