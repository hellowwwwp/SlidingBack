package com.example.slidingback;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public interface SlideInterceptor {

    boolean canSlide(@NonNull MotionEvent ev);

}
