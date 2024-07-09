package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KeywordNewsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword_news)

        val getKeyword = intent.getStringExtra("키워드")
        val setKeyword = findViewById<TextView>(R.id.keywordName)
        setKeyword.text = getKeyword

        val recyclerview = findViewById<RecyclerView>(R.id.newsRecyclerView)

        val previousBtn = findViewById<ImageButton>(R.id.previousBtn)

        previousBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        var newsItem = ArrayList<KeywordNewsModel>()
        val adapter = KeywordNewsAdapter(newsItem)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        val thread: Thread = object : Thread() {
            override fun run() {
                val api: ApiSearchNews = ApiSearchNews
                if (getKeyword != null) {
                    val fetchedNewsItems = api.main(getKeyword)

                    // UI 스레드에서 RecyclerView를 업데이트
                    runOnUiThread {
                        newsItem.clear()
                        newsItem.addAll(fetchedNewsItems)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        thread.start()

    }
}