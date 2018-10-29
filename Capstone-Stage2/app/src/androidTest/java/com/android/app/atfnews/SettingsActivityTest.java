package com.android.app.atfnews;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.app.atfnews.view.SettingsActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class SettingsActivityTest {
    @Rule
    public ActivityTestRule<SettingsActivity> mActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void test0_testScreenComponentsPositive() {
        onView(withId(R.id.second)).check(matches(isDisplayed()));
        onView(withId(R.id.first)).check(matches(isDisplayed()));
        onView(withId(R.id.email_login_text)).check(matches(isDisplayed()));
        onView(withId(R.id.login_with_email)).check(matches(isDisplayed()));
        onView(withId(R.id.logo)).check(matches(withText("ATF News")));
    }

    @Test
    public void test1_testSetup() throws IOException {

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.email_login_text)).perform(replaceText("jpchandru@gmail.com"));
        //onView(withId(R.id.password_field)).perform(replaceText("atfnews"));
        onView(withId(R.id.login_with_email)).perform(click());

    }
}
