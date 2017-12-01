package com.scott.swipelayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.scott.swipe.SwipeLayout

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val swipeLayout = findViewById<SwipeLayout>(R.id.main_swipe_layout)
        findViewById<View>(R.id.main_left_menu).setOnClickListener({
            Toast.makeText(this, "Click sipelayout left menu", Toast.LENGTH_SHORT).show()
        })
        findViewById<View>(R.id.main_right_menu).setOnClickListener({
            Toast.makeText(this, "Click sipelayout right menu", Toast.LENGTH_SHORT).show()
        })
        findViewById<View>(R.id.btn1).setOnClickListener({
            swipeLayout.openStartMenu(true)
        })
        findViewById<View>(R.id.btn2).setOnClickListener({
            swipeLayout.closeStartView(true)
        })
        findViewById<View>(R.id.btn3).setOnClickListener({
            swipeLayout.openEndMenu(true)
        })
        findViewById<View>(R.id.btn4).setOnClickListener({
            swipeLayout.closeEndView(true)
        })
    }
}
