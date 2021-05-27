package com.example.slidingback.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.example.slidingback.AbsSlideBackActivity
import com.example.slidingback.R

class Slide2Activity : AbsSlideBackActivity() {

    private var count: Int = 0

    private val handler = Handler()

    private lateinit var tvTips: TextView

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            count++
            if (count >= 3) {
                tvTips.text = "侧滑启动"
            } else {
                tvTips.text = "侧滑禁用"
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide2)
        tvTips = findViewById(R.id.tv_tips)
        findViewById<TextView>(R.id.tv_title).text = "延时开启侧滑"
        findViewById<TextView>(R.id.tv2).apply {
            text = "Slide2Activity"
            setOnClickListener {
                startActivity(Intent(this@Slide2Activity, Slide3Activity::class.java))
            }
        }

        handler.post(runnable)

        //业务层控制侧滑开关
        setSlideInterceptor { count >= 3 }
    }

    override fun isSlideBackEnabled(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

}