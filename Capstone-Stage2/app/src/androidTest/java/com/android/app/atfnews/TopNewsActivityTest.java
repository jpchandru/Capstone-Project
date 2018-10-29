package com.android.app.atfnews;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import com.android.app.atfnews.view.TopNewsActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anyOf;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TopNewsActivityTest {
    @Rule
    public ActivityTestRule<TopNewsActivity> mActivityTestRule = new ActivityTestRule<>(TopNewsActivity.class);
    private static final String USERNAME = "abcdatfnewstest@gmail.com";
    private static final String TAG = "TopNewsActivityTest.class";

    @Test
    public void test3_testToolbarTextPositive() {
        onView(withId(R.id.email_login_text)).perform(replaceText(USERNAME));
        onView(withId(R.id.login_with_email)).perform(click());
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withText("Top News")).check(matches(withParent(withId(R.id.toolbar))));
    }

    @Test
    public void test4_testToolbarTextNegative() {
        onView(withText("Settings")).check(doesNotExist());
    }

    @Test
    public void test5_testAdViewDisplayPositive() {
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
    }

    @Ignore
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }


    @Test
    public void test6_testInactiveFavIconClickPositive() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread Interruption Exception");
        }
        onView(withIndex(withId(R.id.tv_fav_news_icon_inactive), 0)).perform(click());
    }

    @Test
    public void test7_testActiveFavIconClickPositive() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread Interruption Exception");
        }
        onView(withIndex(withId(R.id.tv_fav_news_icon_active), 0)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread Interruption Exception");
        }
        try {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        } catch (Exception e) {
            Log.e(TAG, "ActionBar Overflow Exception");
        }
        onView(anyOf(withText(R.string.action_settings), withId(R.id.action_settings))).perform(click());
        onView(anyOf(withText(R.string.logout), withId(R.id.btnLogout))).perform(click());
    }
}
