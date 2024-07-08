package com.unit_3.sogong_test

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.contracts.contract

class KeywordRVAdapter (val items : ArrayList<KeywordModel>) : RecyclerView.Adapter<KeywordRVAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.keyword_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: KeywordRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(item: KeywordModel){

            val keywordArea = itemView.findViewById<TextView>(R.id.keywordArea)
            keywordArea.text = item.keyword

            //키워드 클릭 시 키워드 연관 뉴스를 보여주는 페이지로 이동해야함.
            keywordArea.setOnClickListener{
                Toast.makeText(itemView.context, "키워드 클릭: ${item.keyword}", Toast.LENGTH_LONG).show()



                val intent = Intent(itemView.context, KeywordNewsActivity::class.java)
                intent.putExtra("키워드", item.keyword)
                itemView.context.startActivity(intent)
            }

            // ImageButton에 대한 클릭 이벤트 처리 등 추가 작업이 필요하다면 여기에 구현할 수 있습니다.
            val moreVertButton = itemView.findViewById<ImageButton>(R.id.moreVertBtn)
            moreVertButton.setOnClickListener {
                //"키워드 해제" 다이얼로그 혹은 모달 등등..나오게 해야함..
                Toast.makeText(itemView.context, "키워드 해제 버튼 클릭", Toast.LENGTH_SHORT).show()

            }

        }
    }

}