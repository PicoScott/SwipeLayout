package com.scott.swipelayout

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Author: scott
 * Date: 2017/11/14
 * Version: 1.0
 * Description:
 */
class SAdapter() : RecyclerView.Adapter<SAdapter.VH>() {
    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: VH?, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return VH(view)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}
