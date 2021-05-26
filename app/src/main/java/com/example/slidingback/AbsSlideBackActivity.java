package com.example.slidingback;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class AbsSlideBackActivity extends AppCompatActivity implements SlidingPanelLayout.PanelSlideListener {

    private SlidingBackLayout mSlidingBackLayout;
    private SlideInterceptor mSlideInterceptor;
    private boolean isTaskRootSlideEnabled;

    private SlidingPanelLayout.PanelSlideListener mSlideExtraListener;
    private SlidingPanelLayout.PanelSlideListener mSuspensionBallSlideListener;

    public void setTaskRootSlideEnabled(boolean isTaskRootSlideEnabled) {
        this.isTaskRootSlideEnabled = isTaskRootSlideEnabled;
    }

    public void setSlideInterceptor(@NonNull SlideInterceptor slideInterceptor) {
        this.mSlideInterceptor = slideInterceptor;
    }

    public void setSlideExtraListener(@Nullable SlidingPanelLayout.PanelSlideListener listener) {
        this.mSlideExtraListener = listener;
    }

    public void setSuspensionBallSlideListener(@Nullable SlidingPanelLayout.PanelSlideListener listener) {
        this.mSuspensionBallSlideListener = listener;
    }

    @Nullable
    public SlidingBackLayout getSlidingBackLayout() {
        return mSlidingBackLayout;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        applySlideBack();
    }

    public boolean isSlideBackEnabled() {
        return false;
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void applySlideBack() {
        if (!isSlideBackEnabled()) {
            return;
        }
        //横屏下不允许侧滑
        if (isLandscape()) {
            return;
        }
        //默认 task root 的情况下不允许侧滑
        if (!isTaskRootSlideEnabled && isTaskRoot()) {
            return;
        }
        mSlidingBackLayout = new SlidingBackLayout.Builder()
                .attach(this)
                .build();
        mSlidingBackLayout.setSlideInterceptor(mSlideInterceptor);
        mSlidingBackLayout.addPanelSlideListener(this);
    }

    /**
     * 正在侧滑
     */
    @Override
    public void onPanelSlide(@NonNull View panel, float slideOffset) {
        if (mSuspensionBallSlideListener != null) {
            mSuspensionBallSlideListener.onPanelSlide(panel, slideOffset);
        }
        if (mSlideExtraListener != null) {
            mSlideExtraListener.onPanelSlide(panel, slideOffset);
        }
        setPreDecorPosition(slideOffset);
    }

    /**
     * 完全展开,界面退出
     */
    @Override
    public void onPanelOpened(@NonNull View panel) {
        if (mSuspensionBallSlideListener != null) {
            mSuspensionBallSlideListener.onPanelOpened(panel);
        }
        if (mSlideExtraListener != null) {
            mSlideExtraListener.onPanelOpened(panel);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 完全收起,界面重新进入
     */
    @Override
    public void onPanelClosed(@NonNull View panel) {
        if (mSuspensionBallSlideListener != null) {
            mSuspensionBallSlideListener.onPanelClosed(panel);
        }
        if (mSlideExtraListener != null) {
            mSlideExtraListener.onPanelClosed(panel);
        }
    }

    /**
     * 设置上一个界面的位置
     */
    private void setPreDecorPosition(float slideOffset) {
        int displayWidth = getResources().getDisplayMetrics().widthPixels;
        float totalOffset = displayWidth * 0.5f;
        float offset = (slideOffset * totalOffset) - totalOffset;
        Activity preActivity = findPreActivity();
        if (preActivity != null) {
            View decorView = preActivity.getWindow().getDecorView();
            decorView.setX(offset);
        }
    }

    @Nullable
    private Activity findPreActivity() {
        //首先获取倒数第二个 activity
        Activity preActivity = ActivityLifecycleManager.getPenultimateActivity();
        if (preActivity == this) {
            /*
             * 如果倒数第二个 activity 是当前 activity, 那就获取栈顶的 activity
             * 一般出现这种情况是因为倒数第二个 activity 被系统回收了, 然后侧滑拉出的时候又被重新创建了.
             * 所以真正的"倒数第二个" activity 现在变成了栈顶的 activity
             */
            preActivity = ActivityLifecycleManager.getTopActivity();
        }
        //如果获取的倒数第二个 activity 是当前 activity, 那就返回 null 吧
        if (preActivity == this) {
            return null;
        }
        return preActivity;
    }

}
