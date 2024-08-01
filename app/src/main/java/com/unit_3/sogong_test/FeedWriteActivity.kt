package com.unit_3.sogong_test


import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.unit_3.sogong_test.databinding.ActivityFeedWriteBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FeedWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedWriteBinding
    private val TAG = "FeedWriteActivity"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_write)

        // Safely extract extras from the Intent
        val link = intent.getStringExtra("article_link") ?: "" // Default to empty string if null
        val articleTitle = intent.getStringExtra("article_title") ?: "" // Default to empty string if null
        val imageUrl = intent.getStringExtra("article_imageUrl") ?: "" // Default to empty string if null

        Log.d(TAG, "link : $link, title : $articleTitle, imageUrl : $imageUrl")

        val articleTextView: TextView = findViewById(R.id.articleTextView)
        val articleImageArea: ImageView = findViewById(R.id.articleImageArea)

        articleTextView.text = articleTitle

        // Load the image using Glide
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(articleImageArea)
        }

        binding.previousBtn.setOnClickListener {
            finish()
        }

        binding.registerBtn.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            Log.d(TAG, title)
            Log.d(TAG, content)
            val userId = currentUser?.uid ?: "" // Safely handle null currentUser
            val time = getTime()
            val feedRef = database.getReference("feeds")

            // Create a unique ID for the new post
            val newPostRef = feedRef.push()
            val postId = newPostRef.key ?: return@setOnClickListener // Handle null postId

            val feed = FeedModel(postId, userId, title, time, content, articleTitle, link, imageUrl, 0, 0)
            newPostRef.setValue(feed)

            finish()
        }
    }

    private fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREAN).format(currentDateTime)
        return dateFormat
    }
}
