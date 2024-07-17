package com.unit_3.sogong_test

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.unit_3.sogong_test.databinding.ActivityFeedWriteBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FeedWriteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedWriteBinding
    private  val TAG = "FeedWriteActivity"
    private val database = Firebase.database
    private val auth = Firebase.auth
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_write)


        binding.registerBtn.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            Log.d(TAG, title)
            Log.d(TAG, content)
            val userId = currentUser!!.uid
            val time = getTime()
            val feed = database.getReference("feeds")

            feed
                .push()
                .setValue(FeedModel(title, content, userId, time))

            finish()
        }

    }

    fun getTime() : String{
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH:mm:ss", Locale.KOREA).format(currentDateTime)
        return dateFormat
    }



}