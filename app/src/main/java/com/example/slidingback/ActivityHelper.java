package com.example.slidingback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityHelper {

    /**
     * 将 activity 转换为透明
     */
    public static void convertToTranslucent(@NonNull Activity activity, @Nullable TranslucentConversionListener listener) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                convertToTranslucent1(activity, listener);
            } else {
                convertToTranslucent2(activity, listener);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (listener != null) {
                listener.onTranslucentConverted(false);
            }
        }
    }

    @SuppressWarnings("all")
    private static void convertToTranslucent1(@NonNull Activity activity, @Nullable TranslucentConversionListener listener) throws Throwable {
        Method getActivityOptionsMethod = Activity.class.getDeclaredMethod("getActivityOptions");
        getActivityOptionsMethod.setAccessible(true);
        Object options = getActivityOptionsMethod.invoke(activity);

        Class<?> listenerCls = Class.forName("android.app.Activity$TranslucentConversionListener");
        Object proxyListener = Proxy.newProxyInstance(
                Activity.class.getClassLoader(),
                new Class[]{listenerCls},
                new TranslucentConversionInvocationHandler(listener)
        );

        Method convertToTranslucentMethod = Activity.class.getDeclaredMethod("convertToTranslucent", listenerCls, ActivityOptions.class);
        convertToTranslucentMethod.setAccessible(true);
        convertToTranslucentMethod.invoke(activity, proxyListener, options);
    }

    @SuppressWarnings("all")
    private static void convertToTranslucent2(@NonNull Activity activity, @Nullable TranslucentConversionListener listener) throws Throwable {
        Class<?> listenerCls = Class.forName("android.app.Activity$TranslucentConversionListener");
        Object proxyListener = Proxy.newProxyInstance(
                Activity.class.getClassLoader(),
                new Class[]{listenerCls},
                new TranslucentConversionInvocationHandler(listener)
        );

        Method convertToTranslucentMethod = Activity.class.getDeclaredMethod("convertToTranslucent", listenerCls);
        convertToTranslucentMethod.setAccessible(true);
        convertToTranslucentMethod.invoke(activity, proxyListener);
    }

    /**
     * 将 activity 转换为全屏不透明
     */
    @SuppressWarnings("all")
    public static void convertFromTranslucent(@NonNull Activity activity, @Nullable TranslucentConversionListener listener) {
        try {
            Method convertFromTranslucentMethod = Activity.class.getDeclaredMethod("convertFromTranslucent");
            convertFromTranslucentMethod.setAccessible(true);
            convertFromTranslucentMethod.invoke(activity);
            if (listener != null) {
                listener.onTranslucentConverted(false);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (listener != null) {
                listener.onTranslucentConverted(true);
            }
        }
    }

}
