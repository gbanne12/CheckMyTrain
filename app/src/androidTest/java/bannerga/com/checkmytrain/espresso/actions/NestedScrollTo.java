package bannerga.com.checkmytrain.espresso.actions;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

/**
 * Enables scrolling inside a NestedScrollView.  This is a direct copy of Espresso's final
 * {@link androidx.test.espresso.action.ScrollToAction} class.
 * The only change is adding NestedScrollView to the list of constraints as it is not a
 * descendant of ScrollView and as such does not work with that Espresso's existing implementation.
 */
public class NestedScrollTo implements ViewAction {
    private static final String TAG = NestedScrollTo.class.getSimpleName();

    @SuppressWarnings("unchecked")
    @Override
    public Matcher<View> getConstraints() {
        return allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isDescendantOfA(
                        anyOf(
                                isAssignableFrom(ScrollView.class),
                                isAssignableFrom(HorizontalScrollView.class),
                                isAssignableFrom(ListView.class),
                                isAssignableFrom(NestedScrollView.class))));
    }

    @Override
    public void perform(UiController uiController, View view) {
        if (isDisplayingAtLeast(90).matches(view)) {
            Log.i(TAG, "View is already displayed. Returning.");
            return;
        }
        Rect rect = new Rect();
        view.getDrawingRect(rect);
        if (!view.requestRectangleOnScreen(rect, true /* immediate */)) {
            Log.w(TAG, "Scrolling to view was requested, but none of the parents scrolled.");
        }
        uiController.loopMainThreadUntilIdle();
        if (!isDisplayingAtLeast(90).matches(view)) {
            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(
                            new RuntimeException(
                                    "Scrolling to view was attempted, but the view is not " +
                                            "displayed"))
                    .build();
        }
    }

    @Override
    public String getDescription() {
        return "scroll to";
    }
}
