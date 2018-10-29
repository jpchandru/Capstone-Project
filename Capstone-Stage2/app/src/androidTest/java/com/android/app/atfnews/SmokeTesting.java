package com.android.app.atfnews;

import android.support.test.filters.LargeTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginActivityTest.class,
        TopNewsActivityTest.class,
        LogoutActivityTest.class

})

@LargeTest
public class SmokeTesting {

}

