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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class TrendRVAdapter(private val keywords: MutableList<TrendKeywordsModel>) :
    RecyclerView.Adapter<TrendRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankTextView: TextView = view.findViewById(R.id.rankTextView)
        val keywordTextView: TextView = view.findViewById(R.id.keywordTextView)
        val searchCountTextView: TextView = view.findViewById(R.id.searchCountTextView)
        val imageArea: ImageView = view.findViewById(R.id.imageArea)
        val starBtn : ImageView = view.findViewById(R.id.starBtn)
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


            // Initialize Firebase
            val auth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance().getReference("keyword").child(auth.currentUser!!.uid)

            // Check if the keyword is already starred
            database.orderByChild("keyword").equalTo(keyword.keyword).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        starBtn.setImageResource(R.drawable.star)
                    } else {
                        starBtn.setImageResource(R.drawable.star_trans)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TrendRVAdapter", "onCancelled", databaseError.toException())
                }
            })

            starBtn.setOnClickListener {
                database.orderByChild("keyword").equalTo(keyword.keyword).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // If the keyword is already starred, remove it
                            for (snapshot in dataSnapshot.children) {
                                snapshot.ref.removeValue()
                            }
                            Toast.makeText(context, "나의 키워드에서 ${keyword.keyword} 삭제했습니다.", Toast.LENGTH_LONG).show()
                            starBtn.setImageResource(R.drawable.star_trans)
                        } else {
                            // If the keyword is not starred, add it
                            val newKeyword = KeywordModel(keyword.keyword, "", true, keyword.imageUrl)
                            database.push().setValue(newKeyword)
                            Toast.makeText(context, "나의 키워드에 ${keyword.keyword} 등록했습니다.", Toast.LENGTH_LONG).show()
                            starBtn.setImageResource(R.drawable.star)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("TrendRVAdapter", "onCancelled", databaseError.toException())
                    }
                })
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
