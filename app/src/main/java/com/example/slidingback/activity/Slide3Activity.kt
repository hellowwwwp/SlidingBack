package com.example.slidingback.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.slidingback.AbsSlideBackActivity
import com.example.slidingback.R

class Slide3Activity : AbsSlideBackActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide3)
        findViewById<TextView>(R.id.tv_title).text = "ViewPager场景"

        //ViewPager 和 ViewPager2 都已测试通过
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)
        viewPager2.adapter = MyAdapter()
    }

    class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.layout_slide_item_view, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.contentView.findViewById<TextView>(R.id.tv).text = "slide item: $position"
        }

        override fun getItemCount(): Int {
            return 2
        }

    }

    class MyViewHolder(val contentView: View) : RecyclerView.ViewHolder(contentView)

    override fun isSlideBackEnabled(): Boolean {
        return true
    }

}