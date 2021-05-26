package com.example.slidingback.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        Log.e("tag", "Slide1Activity onCreate")
    }

    override fun isSlideBackEnabled(): Boolean {
        return true
    }

    override fun onStart() {
        super.onStart()
        Log.e("tag", "Slide1Activity onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.e("tag", "Slide1Activity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("tag", "Slide1Activity onDestroy")
    }

}