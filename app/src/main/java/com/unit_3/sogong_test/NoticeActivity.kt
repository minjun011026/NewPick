package com.unit_3.sogong_test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.unit_3.sogong_test.databinding.ActivityNoticeBinding

class NoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 공지사항 데이터를 Firebase에서 불러와 표시
        loadNoticesFromFirebase()
    }

    private fun loadNoticesFromFirebase() {
        val database = Firebase.database
        val noticeRef = database.getReference("notices")

        noticeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notices = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                binding.textViewNotices.text = notices.joinToString("\n\n")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NoticeActivity", "Database error: ${error.message}")
            }
        })
    }
}
