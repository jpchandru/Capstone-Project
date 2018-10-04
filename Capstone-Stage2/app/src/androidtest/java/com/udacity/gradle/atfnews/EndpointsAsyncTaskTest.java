package com.udacity.gradle.atfnews;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by cj on 9/20/18.
 */

@RunWith(AndroidJUnit4.class)
public class EndpointsAsyncTaskTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivity = new ActivityTestRule(MainActivity.class);

    /**
     * This test is a connected test to verify loading jokes via button click
     */
    @Test
    public void jokeTextTestPositive() {
        final List<String> jokes = new ArrayList<String>();
        jokes.add("Laugh Out Loudly");
        jokes.add("Laugh Out Really Loud");
        jokes.add("Comeon Snake, Laugh Out Really Loud");

        Espresso.onView(withId(R.id.button_view)).perform(ViewActions.click());

        Matcher matcher = new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View items) {
                for (String joke : jokes) {
                    View item = items.findViewById(R.id.random_joke);
                    String jokeTxtFromScreen = null;
                    if (item != null) jokeTxtFromScreen = ((TextView) item).getText().toString();
                    if (jokeTxtFromScreen != null && jokeTxtFromScreen.equalsIgnoreCase(joke))
                        return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
        Espresso.onView(withId(R.id.random_joke)).check(matches(isDisplayed()));
        onView(withId(R.id.random_joke)).check(matches(matcher));
    }

    
    @Test
    public void jokeTextTestNegative() {
        Espresso.onView(withId(R.id.button_view)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.random_joke)).check(matches(isDisplayed()));
        onView(withText("Angry")).check(doesNotExist());
    }


}
