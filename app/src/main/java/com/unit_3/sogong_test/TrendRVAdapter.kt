package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrendRVAdapter(private val keywords: List<String>?) :
    RecyclerView.Adapter<TrendRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keywordTextView: TextView = view.findViewById(R.id.keywordTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trend_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.keywordTextView.text = keywords!![position]
    }

    override fun getItemCount() = keywords!!.size
}
