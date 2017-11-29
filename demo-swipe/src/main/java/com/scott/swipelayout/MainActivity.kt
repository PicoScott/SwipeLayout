package com.scott.swipelayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.scott.swipe.SwipeLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SAdapter()

//        findViewById<SwipeLayout>(R.id.xyz).setRangeChangeListener {
//            run {
//                Log.d("abc", it.toString())
//            }
//        }
//        val sView = findViewById<SView>(R.id.sview)
//        findViewById<View>(R.id.top).setOnClickListener({ sview.pullTop() })
//        findViewById<View>(R.id.bottom).setOnClickListener({ sview.pullBottom() })
    }
}
