package com.example.slidingback;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityLifecycleManager {

    private static final LinkedList<Activity> sActivityStack = new LinkedList<>();

    private static final Application.ActivityLifecycleCallbacks sCallbacks = new SimpleActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            pushActivity(activity);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            popActivity(activity);
        }
    };

    public static void init(@NonNull Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(sCallbacks);
    }

    private static void pushActivity(@NonNull Activity activity) {
        sActivityStack.add(activity);
    }

    private static void popActivity(@NonNull Activity activity) {
        final int size = sActivityStack.size();
        int index = -1;
        for (int i = size - 1; i >= 0; i--) {
            Activity item = sActivityStack.get(i);
            if (item == activity) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            sActivityStack.remove(index);
        }
    }

    @Nullable
    public static Activity getTopActivity() {
        return sActivityStack.getLast();
    }

    @Nullable
    public static Activity getPenultimateActivity() {
        if (sActivityStack.size() >= 2) {
            return sActivityStack.get(sActivityStack.size() - 2);
        }
        return null;
    }

}
