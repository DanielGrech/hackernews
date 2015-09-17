package com.dgsd.android.hackernews;

/**
 * App class used to run Robolectric tests.
 */
@SuppressWarnings("unused")
public class TestHNAppImpl extends HNAppImpl {

    @Override
    protected void enableDebugTools() {
        // Not whilst running tests
    }

    @Override
    protected void enableAppOnlyFunctionality() {
        // Not whilst running tests
    }
}
