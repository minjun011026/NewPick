package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookmarkedNewsActivity : AppCompatActivity() {

    private lateinit var newsItem: ArrayList<BookmarkedNewsModel>
    private lateinit var adapter: BookmarkedNewsAdapter
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var previousBtn: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmarked_news)

        dbHelper = DatabaseHelper()
        newsItem = ArrayList()
        adapter = BookmarkedNewsAdapter(this, newsItem)

        previousBtn = findViewById(R.id.previousBtn)
        recyclerView = findViewById(R.id.recyclerViewBookmarkedNews)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadBookmarkedNews()

        setupPreviousButton()
    }

    private fun loadBookmarkedNews() {
        dbHelper.getAllBookmarkedNews { list ->
            newsItem.clear()
            newsItem.addAll(list)
            adapter.notifyDataSetChanged()
        }

        // Handle item click on RecyclerView
        adapter.setOnItemClickListener { position ->
            val selectedNews = newsItem[position]
            //  openNewsDetailActivity(selectedNews.link)
        }
    }

    private fun setupPreviousButton() {
        previousBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    // private fun openNewsDetailActivity(newsUrl: String) {
    //  val intent = Intent(this, NewsDetailActivity::class.java)
    //  intent.putExtra("newsUrl", newsUrl)
    //  startActivity(intent)
    // }
}
