package bannerga.com.checkmytrain.view.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import bannerga.com.checkmytrain.R
import bannerga.com.checkmytrain.view.actions.NestedScrollTo
import bannerga.com.checkmytrain.view.matchers.UiMatcher.first

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
