package com.unit_3.sogong_test

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.unit_3.sogong_test.databinding.ActivityFeedWrite2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale

class FeedWriteActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityFeedWrite2Binding
    private val TAG = "FeedWriteActivity2"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private var articleTitle = ""
    private var imageUrl = ""
    private var link = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_write2)

       binding.linkEditText.addTextChangedListener { editable ->
            link = editable?.toString() ?: ""
            Log.d(TAG, "Link updated: $link")
            if (link.isNotEmpty()) {
                fetchArticleDetails(link)
            } else {
                clearArticleDetails()
            }
        }


        binding.previousBtn.setOnClickListener {
            finish()
        }

        binding.registerBtn.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            Log.d(TAG, "Title: $title")
            Log.d(TAG, "Content: $content")
            val userId = currentUser!!.uid
            val time = getTime()
            val feedRef = database.getReference("feeds")

            // Create a unique ID for the new post
            val newPostRef = feedRef.push()
            val postId = newPostRef.key

            // Ensure the postId is not null
            if (postId != null) {
                val feed = FeedModel(postId, userId, title, time, content, articleTitle, link, imageUrl, 0, 0)
                newPostRef.setValue(feed)
            }

            finish()
        }
    }

    private fun fetchArticleDetails(link: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val doc = Jsoup.connect(link).get()
                articleTitle = doc.select("#title_area > span").text()
                val imgElement = doc.select("#img1")
                imageUrl = imgElement.attr("data-src")

                Log.d(TAG, "Article Title: $articleTitle")
                Log.d(TAG, "Image URL: $imageUrl")
                Log.d(TAG, "Image Element HTML: ${imgElement.outerHtml()}")

                withContext(Dispatchers.Main) {
                    if (articleTitle.isNotEmpty() && imageUrl.isNotEmpty()) {

                        binding.articleTextView.isVisible = true
                        binding.articleImageArea.isVisible = true

                        binding.articleTextView.text = articleTitle
                        binding.articleImageArea.isGone = false
                        Glide.with(this@FeedWriteActivity2).load(imageUrl).into(binding.articleImageArea)
                    } else {
                        binding.articleTextView.isGone = true
                        binding.articleImageArea.isGone = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FeedWriteActivity2, "Failed to load article details", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error fetching article details: $e")
                }
            }
        }
    }

    private fun clearArticleDetails() {
        articleTitle = ""
        imageUrl = ""
        binding.articleTextView.text = ""
        binding.articleTextView.isGone = true
        binding.articleImageArea.isGone = true
    }


    private fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREAN).format(currentDateTime)
        return dateFormat
    }
}
