package com.niesens.ariak.test;

import android.test.ActivityInstrumentationTestCase2;
import com.niesens.ariak.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public HelloAndroidActivityTest() {
        super(MainActivity.class);
    }

    public void testActivity() {
        MainActivity activity = getActivity();
        assertNotNull(activity);
    }
}

