package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fragments.SummaryDialogFragment

class KeywordNewsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword_news)

        val url = intent.getStringExtra("link")
        if (url != null) {
            val fragment = SummaryDialogFragment.newInstance(url)
            fragment.show(supportFragmentManager, "SummaryFragment")
        }

        val getKeyword = intent.getStringExtra("키워드")
        val setKeyword = findViewById<TextView>(R.id.keywordName)
        setKeyword.text = getKeyword

        val recyclerview = findViewById<RecyclerView>(R.id.newsRecyclerView)

        val previousBtn = findViewById<ImageButton>(R.id.previousBtn)

        previousBtn.setOnClickListener {
            // 이전 화면으로 돌아가기 위해 Intent에 FLAG_ACTIVITY_CLEAR_TOP 플래그 추가
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("키워드", getKeyword)  // 키워드 정보 다시 전달
            intent.putExtra("link", url)          // 뉴스 링크 정보 다시 전달
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
                        fetchedNewsItems.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        thread.start()

    }
}

