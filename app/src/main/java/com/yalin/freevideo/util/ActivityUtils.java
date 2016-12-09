package com.yalin.freevideo.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;

/**
 * YaLin
 * 2016/12/9.
 */

public class ActivityUtils {
    public static void createBackStack(Activity activity, Intent intent) {
        if (Build.VERSION.SDK_INT >= 16) {
            TaskStackBuilder builder = TaskStackBuilder.create(activity);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
