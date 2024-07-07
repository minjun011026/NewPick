package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TrendRVAdapter(private val keywords: List<String>?) :
    RecyclerView.Adapter<TrendRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keywordTextView: TextView = view.findViewById(R.id.keywordTextView)

        fun bind(keyword: String, context: Context) {
            Log.d("TrendRVAdapter", "bind: $keyword")
            keywordTextView.text = keyword

            itemView.setOnClickListener {
                Toast.makeText(context, "키워드 클릭: $keyword", Toast.LENGTH_LONG).show()
                val intent = Intent(context, TrendKeywordActivity::class.java)
                intent.putExtra("keyword", keyword)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trend_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        keywords?.get(position)?.let { holder.bind(it, holder.itemView.context) }
    }

    override fun getItemCount(): Int {
        return keywords?.size ?: 0
    }
}
