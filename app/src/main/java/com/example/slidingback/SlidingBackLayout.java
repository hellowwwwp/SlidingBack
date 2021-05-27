package com.example.slidingback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.fragment.app.FragmentActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SlidingBackLayout extends SlidingPanelLayout implements TranslucentConversionListener, SlidingPanelLayout.PanelSlideListener {

    private WeakReference<Activity> mActivityRefs;

    /**
     * 侧滑拦截器, 允许上层随时拦截
     */
    private SlideInterceptor mSlideInterceptor;

    /**
     * 遮罩层
     */
    private final View mMaskView;

    /**
     * 当 activity 重新进入时是否设置为不透明
     */
    private boolean isActivityNoTransparentWhenReenter = true;

    /**
     * 是否强制 activity 为透明
     */
    private boolean isForceActivityTransparent = false;

    public SlidingBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SlidingBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mMaskView = new View(context);
        mMaskView.setClickable(true);
    }

    public void setMaskColor(@ColorInt int maskColor) {
        mMaskView.setBackgroundColor(maskColor);
    }

    public void setSlideInterceptor(@Nullable SlideInterceptor interceptor) {
        mSlideInterceptor = interceptor;
    }

    public void setActivityNoTransparentWhenReenter(boolean isActivityNoTransparentWhenReenter) {
        this.isActivityNoTransparentWhenReenter = isActivityNoTransparentWhenReenter;
    }

    public void setForceActivityTransparent(boolean isForceActivityTransparent) {
        this.isForceActivityTransparent = isForceActivityTransparent;
    }

    /**
     * @return true 放弃触摸事件,否则正常响应触摸事件
     */
    private boolean tryAbandonTouchEvent(MotionEvent ev) {
        if (isCanSlide()) {
            final SlideInterceptor interceptor = mSlideInterceptor;
            return interceptor != null && !interceptor.canSlide(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = ev.getActionMasked();
//        if (action == MotionEvent.ACTION_DOWN && mSlideOffset != 0f) {
//            //侧滑面板已经有滑动了,放弃新来的触摸事件序列
//            return false;
//        }

        if (ev.getPointerCount() > 1) {
            return false;
        }
        if (tryAbandonTouchEvent(ev)) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (tryAbandonTouchEvent(ev)) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    public void attachToActivity(@NonNull Activity activity) {
        this.mActivityRefs = new WeakReference<>(activity);
        attachSlideView(activity.findViewById(android.R.id.content));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && activity.getWindow() != null) {
            //设置 activity 背景为透明
            activity.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            //不强制 activity 透明就把 activity 转为不透明
            if (!isForceActivityTransparent) {
                convertFromTranslucent();
            }
        }
    }

    private void attachSlideView(@NonNull View contentView) {
        if (getChildCount() != 0) {
            removeAllViews();
        }

        final boolean isFocused = contentView.isFocused();

        final ViewGroup parent = (ViewGroup) contentView.getParent();
        //将 contentView 从之前的 ViewGroup 中移除
        parent.removeView(contentView);

        final LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        addView(mMaskView, layoutParams);
        addView(contentView, layoutParams);

        //重新将侧滑面板加入到之前的 ViewGroup 中
        parent.addView(this, contentView.getLayoutParams());

        //如果之前的 contentView 有焦点那就让它重新获取
        if (isFocused) {
            requestFocus();
        }

        //添加侧滑监听
        addPanelSlideListener(this);
    }

    /**
     * 正在侧滑
     */
    @Override
    public void onPanelSlide(@NonNull View panel, float slideOffset) {
        //实时设置遮罩层的透明度
        float alpha = MathUtils.clamp((1.0f - slideOffset), 0.0f, 1.0f);
        mMaskView.setAlpha(alpha);
    }

    /**
     * 完全展开,界面退出
     */
    @Override
    public void onPanelOpened(@NonNull View panel) {
        //no op
    }

    /**
     * 完全收起,界面重新进入
     */
    @Override
    public void onPanelClosed(@NonNull View panel) {
        if (isActivityNoTransparentWhenReenter && !isForceActivityTransparent) {
            convertFromTranslucent();
        }
    }

    @Nullable
    private Activity getActivity() {
        WeakReference<Activity> activityRefs = mActivityRefs;
        if (activityRefs != null) {
            return activityRefs.get();
        }
        return null;
    }

    @Override
    public void convertToTranslucent() {
        final Activity activity = getActivity();
        if (activity != null) {
            ActivityHelper.convertToTranslucent(activity, this);
        }
    }

    @Override
    public void convertFromTranslucent() {
        final Activity activity = getActivity();
        if (activity != null) {
            ActivityHelper.convertFromTranslucent(activity, this);
        }
    }

    @Override
    public void onTranslucentConverted(boolean isTranslucent) {
        setActivityTransparent(isTranslucent);
    }

    public static class Builder {

        private FragmentActivity mActivity;

        /**
         * 侧滑面板阴影
         */
        private Drawable mShadowDrawable;

        /**
         * 侧滑面板阴影
         */
        @DrawableRes
        private int mShadowResource = R.drawable.sliding_layout_shadow;

        /**
         * 遮罩层颜色
         */
        @ColorInt
        private int mMaskColor = Color.parseColor("#40000000");

        private float mSlideRegionFactor = 1.0f;

        private float mForceSlideRegionFactor = 0.25f;

        /**
         * 当 activity 重新进入时是否设置为不透明
         */
        private boolean isActivityNoTransparentWhenReenter = true;

        /**
         * 是否强制 activity 为透明
         */
        private boolean isForceActivityTransparent = false;

        public Builder attach(@NonNull FragmentActivity activity) {
            this.mActivity = activity;
            return this;
        }

        public Builder setShadowDrawable(@NonNull Drawable d) {
            this.mShadowDrawable = d;
            return this;
        }

        public Builder setShadowResource(@DrawableRes int resId) {
            this.mShadowResource = resId;
            return this;
        }

        public Builder setMaskColor(@ColorInt int color) {
            this.mMaskColor = color;
            return this;
        }

        public Builder setSlideRegionFactor(@FloatRange(from = 0.0d, to = 1.0d) float slideRegionFactor) {
            this.mSlideRegionFactor = slideRegionFactor;
            return this;
        }

        public Builder setForceSlideRegionFactor(@FloatRange(from = 0.0d, to = 1.0d) float forceSlideRegionFactor) {
            this.mForceSlideRegionFactor = forceSlideRegionFactor;
            return this;
        }

        public Builder setActivityNoTransparentWhenReenter(boolean isActivityNoTransparentWhenReenter) {
            this.isActivityNoTransparentWhenReenter = isActivityNoTransparentWhenReenter;
            return this;
        }

        public Builder setForceActivityTransparent(boolean isForceActivityTransparent) {
            this.isForceActivityTransparent = isForceActivityTransparent;
            return this;
        }

        public SlidingBackLayout build() {
            FragmentActivity activity = mActivity;
            if (activity == null) {
                throw new IllegalArgumentException("activity must not be null");
            }
            Drawable shadowDrawable = mShadowDrawable;
            if (shadowDrawable == null) {
                shadowDrawable = ContextCompat.getDrawable(activity, mShadowResource);
            }
            final SlidingBackLayout layout = new SlidingBackLayout(activity);
            layout.setShadowDrawable(shadowDrawable);
            layout.setSliderFadeColor(Color.TRANSPARENT);
            layout.setMaskColor(mMaskColor);
            layout.setSlideRegionFactor(mSlideRegionFactor);
            layout.setForceSlideRegionFactor(mForceSlideRegionFactor);
            layout.setActivityNoTransparentWhenReenter(isActivityNoTransparentWhenReenter);
            layout.setForceActivityTransparent(isForceActivityTransparent);
            layout.attachToActivity(activity);
            return layout;
        }
    }
}
