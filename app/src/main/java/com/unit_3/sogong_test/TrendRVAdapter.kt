package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrendRVAdapter(private val keywords: MutableList<TrendKeywordsModel>) :
    RecyclerView.Adapter<TrendRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keywordTextView: TextView = view.findViewById(R.id.keywordTextView)
        val searchCountTextView: TextView = view.findViewById(R.id.searchCountTextView)
        val imageArea: ImageView = view.findViewById(R.id.imageArea)


        fun bind(keyword: TrendKeywordsModel, context: Context) {
            Log.d("TrendRVAdapter", "bind: $keyword")
//            keywordTextView.text = keyword.toString()
//            searchCountTextView.text = searchCountTextView.toString()
            keywordTextView.text = keyword.keyword
            searchCountTextView.text = keyword.searchCount

            Glide.with(context)
                .load(keyword.imageUrl)
                .into(imageArea)

            itemView.setOnClickListener {
//                Toast.makeText(context, "키워드 클릭: $keyword", Toast.LENGTH_LONG).show()
//                val intent = Intent(context, TrendKeywordActivity::class.java)
//                intent.putExtra("keyword", keyword)
                Toast.makeText(context, "키워드 클릭: ${keyword}", Toast.LENGTH_LONG).show()
                val intent = Intent(context, TrendKeywordActivity::class.java)
                intent.putExtra("keyword_title", keyword.keyword)  // 필요한 필드만을 전달
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
//        keywords[position].let { holder.bind(it, holder.itemView.context) }
        val keyword = keywords[position]
        holder.bind(keyword, holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return keywords.size
    }
}
