package bannerga.com.checkmytrain.view.robot

import android.view.View
import android.widget.TimePicker

import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers

import org.hamcrest.Matcher
import org.hamcrest.Matchers

import bannerga.com.checkmytrain.R

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.anything

class JourneyRobot {

    private val departureInput = withId(R.id.departure_station_input)
    private val arrivalInput = withId(R.id.arrival_station_input)
    private val timeInput = withId(R.id.time_input)
    private val timePicker = withClassName(Matchers.equalTo(TimePicker::class.java.name))
    private val timePickerSubmit = withId(android.R.id.button1)
    private val journeySubmit = withId(R.id.button_submit)

    fun setJourney(departing: String, arriving: String, hour: Int, minutes: Int): JourneyRobot {
        onView(departureInput).perform(typeText(departing))
        onView(arrivalInput).perform(typeText(arriving))
        onData(anything())
                .inRoot(RootMatchers.isPlatformPopup())
                .atPosition(1)
                .perform(click())
        onView(timeInput).perform(click())
        onView(timePicker).perform(PickerActions.setTime(hour, minutes))
        onView(timePickerSubmit).perform(click())
        onView(journeySubmit).perform(click())
        return this
    }

}
