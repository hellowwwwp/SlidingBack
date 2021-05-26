package com.example.slidingback;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TranslucentConversionInvocationHandler implements InvocationHandler {

    @Nullable
    private final TranslucentConversionListener mListener;

    public TranslucentConversionInvocationHandler(@Nullable TranslucentConversionListener listener) {
        this.mListener = listener;
    }

    @Override
    public Object invoke(@NonNull Object obj, Method method, @Nullable Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        if (mListener != null && args != null) {
            mListener.onTranslucentConverted((boolean) args[0]);
        }
        return null;
    }

}
