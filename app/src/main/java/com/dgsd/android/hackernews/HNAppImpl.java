package com.dgsd.android.hackernews;

import android.support.annotation.NonNull;

import com.dgsd.android.hackernews.module.AppServicesComponent;
import com.dgsd.android.hackernews.module.DaggerAppServicesComponent;

public class HNAppImpl extends HNApp {

    @NonNull
    @Override
    protected AppServicesComponent createAppServicesComponent() {
        return DaggerAppServicesComponent.builder()
                .hNModule(getModule())
                .build();
    }
}
