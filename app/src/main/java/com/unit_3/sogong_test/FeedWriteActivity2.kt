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
import com.unit_3.sogong_test.databinding.ActivityFeedWrite2Binding
import java.text.SimpleDateFormat
import java.util.Locale

class FeedWriteActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityFeedWrite2Binding
    private val TAG = "FeedWriteActivity2"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_write2)


        binding.previousBtn.setOnClickListener {
            finish()
        }



        binding.registerBtn.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val link = binding.linkEditText?.text.toString()
            Log.d(TAG, title)
            Log.d(TAG, content)
            val userId = currentUser!!.uid
            val time = getTime()
            val feedRef = database.getReference("feeds")

            // Create a unique ID for the new post
            val newPostRef = feedRef.push()
            val postId = newPostRef.key

            // Ensure the postId is not null
            if (postId != null) {
                val feed = FeedModel(postId, userId, title, time, content, "", link, "", 0, 0)
                newPostRef.setValue(feed)
            }

            finish()
        }
    }

    private fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREAN).format(currentDateTime)
        return dateFormat
    }
}
