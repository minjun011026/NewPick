package com.unit_3.sogong_test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.widget.TextView

class NoticeActivity : AppCompatActivity() {

    private lateinit var previousBtn: ImageButton
    private lateinit var textViewNotices: TextView
    private lateinit var textView: TextView
    private lateinit var recyclerViewBookmarkedNews: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice) // Ensure this matches your XML layout file name

        // Initialize UI components
        previousBtn = findViewById(R.id.previousBtn)
        textViewNotices = findViewById(R.id.textViewNotices)
        textView = findViewById(R.id.textview)
        recyclerViewBookmarkedNews = findViewById(R.id.recyclerViewBookmarkedNews)

        // Add any additional setup or functionality here
    }
}
