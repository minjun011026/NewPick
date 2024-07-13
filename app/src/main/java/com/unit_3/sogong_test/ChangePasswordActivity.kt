package com.unit_3.sogong_test

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var editTextPassword: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSave = findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val newPassword = editTextPassword.text.toString()
            if (newPassword.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("password", newPassword)
                editor.apply()
                Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
