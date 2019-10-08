package bannerga.com.checkmytrain.espresso.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import bannerga.com.checkmytrain.R
import bannerga.com.checkmytrain.espresso.actions.NestedScrollTo
import bannerga.com.checkmytrain.espresso.matchers.UiMatcher.first

class CardsRobot {

    private val cardContainer = withId(R.id.card_container)
    private val departureStationText = withId(R.id.departureStationText)
    private val arrivalStationText = withId(R.id.arrivalStationText)

    fun checkCardIsDisplayed(departureStation: String, arrivalStation: String): CardsRobot {
        onView(first(cardContainer)).perform(ViewActions.actionWithAssertions(NestedScrollTo()))
        onView(first(departureStationText)).check(matches(withText(departureStation)))
        onView(first(arrivalStationText)).check(matches(withText(arrivalStation)))
        return this
    }
}
