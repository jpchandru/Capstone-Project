package com.android.app.atfnews;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.app.atfnews.view.LoginActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anyOf;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private static final String TAG = "LoginActivityTest.class";
    private static final String USERNAME = "abcdatfnewstest@gmail.com";

    @Test
    public void test0_logout() throws NoMatchingViewException {

        try {
            try {
                openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            } catch (Exception e) {
                //This is normal. Maybe we dont have overflow menu.
                Log.e(TAG, "ActionBar Exception");
            }
            onView(anyOf(withText(R.string.action_settings), withId(R.id.action_settings))).perform(click());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread Interruption Exception");
            }
            onView(anyOf(withText(R.string.logout), withId(R.id.btnLogout))).perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread Interruption Exception");
        }
    }

    @Test
    public void test1_testScreenComponentsPositive() {
        onView(withId(R.id.second)).check(matches(isDisplayed()));
        onView(withId(R.id.first)).check(matches(isDisplayed()));
        onView(withId(R.id.email_login_text)).check(matches(isDisplayed()));
        onView(withId(R.id.login_with_email)).check(matches(isDisplayed()));
        onView(withId(R.id.logo)).check(matches(withText("ATF News")));
    }

    @Test
    public void test2_testSetup() throws IOException {

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread Interruption Exception");
        }
        onView(withId(R.id.email_login_text)).perform(replaceText(USERNAME));
        onView(withId(R.id.login_with_email)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread Interruption Exception");
        }
        test0_logout();

    }


}
