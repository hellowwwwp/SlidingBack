package com.example.slidingback.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.example.slidingback.AbsSlideBackActivity
import com.example.slidingback.R

class Slide1Activity : AbsSlideBackActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide1)
        findViewById<TextView>(R.id.tv_title).text = "首页(TaskRoot)"
        findViewById<TextView>(R.id.tv1).apply {
            text = "Slide1Activity"
            setOnClickListener {
                startActivity(Intent(this@Slide1Activity, Slide2Activity::class.java))
            }
        }
    }

    override fun isSlideBackEnabled(): Boolean {
        //task root 的情况下默认不允许侧滑返回
        return true
    }

}