package bannerga.com.checkmytrain.view.matchers;

import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class UiMatcher {

    public static Matcher<View> first(final Matcher<View> matcher) {
        return new BaseMatcher<View>() {
            boolean isFirst = true;

            @Override
            public void describeTo(Description description) {
                description.appendText("the first element matching: ");
                matcher.describeTo(description);
            }

            @Override
            public boolean matches(Object element) {
                if (isFirst && matcher.matches(element)) {
                    isFirst = false;
                    return true;
                }
                return false;
            }
        };
    }

}
