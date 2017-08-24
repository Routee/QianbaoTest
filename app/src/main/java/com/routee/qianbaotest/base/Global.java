package com.routee.qianbaotest.base;

import android.content.Context;

/**
 * Created by Routee on 2017/8/12.
 * description: ${cusor}
 */

public class Global {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }
}
