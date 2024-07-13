package com.unit_3.sogong_test

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ChangeNicknameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname)

        val nicknameEditText: EditText = findViewById(R.id.nicknameEditText)
        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val newNickname = nicknameEditText.text.toString()
            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("nickname", newNickname)
                apply()
            }
            finish() // 액티비티 종료
        }
    }
}
