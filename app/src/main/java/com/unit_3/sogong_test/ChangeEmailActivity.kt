package com.unit_3.sogong_test

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        editTextEmail = findViewById(R.id.editTextEmail)
        buttonSave = findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val newEmail = editTextEmail.text.toString()
            if (newEmail.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("email", newEmail)
                editor.apply()
                Toast.makeText(this, "이메일이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
