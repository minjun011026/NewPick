package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class VerifyCurrentPasswordActivity : AppCompatActivity() {

    private lateinit var editTextCurrentPassword: EditText
    private lateinit var buttonNext: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_current_password)

        firebaseAuth = FirebaseAuth.getInstance()

        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword2)
        buttonNext = findViewById(R.id.buttonNext2)

        buttonNext.setOnClickListener {
            val currentPassword = editTextCurrentPassword.text.toString()
            if (currentPassword.isNotEmpty()) {
                val user = firebaseAuth.currentUser
                val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, ChangeNewPasswordActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
