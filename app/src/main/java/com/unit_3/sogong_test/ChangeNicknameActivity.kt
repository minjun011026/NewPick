package com.unit_3.sogong_test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangeNicknameActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val editTextNickname = findViewById<EditText>(R.id.editTextNickname)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val newNickname = editTextNickname.text.toString().trim()
            val currentPassword = editTextPassword.text.toString().trim()

            if (newNickname.isNotEmpty() && currentPassword.isNotEmpty()) {
                reauthenticateAndChangeNickname(currentPassword, newNickname)
            } else {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reauthenticateAndChangeNickname(password: String, nickname: String) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return

        val credential = EmailAuthProvider.getCredential(email, password)

        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveNickname(nickname)
            } else {
                Toast.makeText(this, "비밀번호 인증 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNickname(nickname: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userId).child("nickname")

        userRef.setValue(nickname)
            .addOnSuccessListener {
                val resultIntent = Intent().apply {
                    putExtra("nickname", nickname)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                Toast.makeText(this, "닉네임이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "닉네임 변경에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
