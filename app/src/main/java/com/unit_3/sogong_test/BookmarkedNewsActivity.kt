package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookmarkedNewsActivity : AppCompatActivity() {

    private lateinit var newsItem: ArrayList<BookmarkedNewsModel>
    private lateinit var adapter: BookmarkedNewsAdapter
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var previousBtn: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var noBookmarkedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmarked_news)

        dbHelper = DatabaseHelper()
        newsItem = ArrayList()
        adapter = BookmarkedNewsAdapter(this, newsItem)

        previousBtn = findViewById(R.id.backBtn)
        recyclerView = findViewById(R.id.rv)
        noBookmarkedTextView = findViewById(R.id.no_bookmarked_textview)

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

            if (newsItem.isEmpty()) {
                noBookmarkedTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noBookmarkedTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        // Handle item click on RecyclerView
        adapter.setOnItemClickListener { position ->
            val selectedNews = newsItem[position]
            // openNewsDetailActivity(selectedNews.link)
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
}
