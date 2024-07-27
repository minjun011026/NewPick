package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class TrendRVAdapter(private val keywords: MutableList<TrendKeywordsModel>) :
    RecyclerView.Adapter<TrendRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankTextView: TextView = view.findViewById(R.id.rankTextView)
        val keywordTextView: TextView = view.findViewById(R.id.keywordTextView)
        val searchCountTextView: TextView = view.findViewById(R.id.searchCountTextView)
        val imageArea: ImageView = view.findViewById(R.id.imageArea)
        val addKeywordBtn : ImageView = view.findViewById(R.id.addKeywordBtn)
        val trendItem : LinearLayout = view.findViewById(R.id.trendItem)

        fun bind(keyword: TrendKeywordsModel, context: Context, position: Int) {
            Log.d("TrendRVAdapter", "bind: $keyword")
            rankTextView.text = "${position + 1}위"
            keywordTextView.text = keyword.keyword
            searchCountTextView.text = "${keyword.searchCount}회 검색"

            Glide.with(context)
                .load(keyword.imageUrl)
                .into(imageArea)

            trendItem.setOnClickListener {
                Toast.makeText(context, "키워드 클릭: ${keyword}", Toast.LENGTH_LONG).show()
                val intent = Intent(context, TrendKeywordActivity::class.java)
                intent.putExtra("keyword_title", keyword.keyword)  // 필요한 필드만을 전달
                context.startActivity(intent)
            }

            addKeywordBtn.setOnClickListener {
                val keyword = keyword.keyword

                if(keyword != null){
                    val database = Firebase.database
                    val myRef = database.getReference("keyword").child(Firebase.auth.currentUser!!.uid)
                    myRef.push().setValue(KeywordModel(keyword.toString()))
                    Toast.makeText(context,"나의 키워드에 ${keyword} 등록했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trend_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keyword = keywords[position]
        holder.bind(keyword, holder.itemView.context, position)
    }

    override fun getItemCount(): Int {
        return keywords.size
    }
}
