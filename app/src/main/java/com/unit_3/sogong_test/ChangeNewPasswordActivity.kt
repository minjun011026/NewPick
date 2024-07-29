package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ChangeNewPasswordActivity : AppCompatActivity() {

    private lateinit var editTextNewPassword: EditText
    private lateinit var editTextConfirmNewPassword: EditText
    private lateinit var buttonSaveNewPassword: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password) // Referencing activity_change_password.xml

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Bind UI elements
        editTextNewPassword = findViewById(R.id.editTextNewPassword1)
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword1)
        buttonSaveNewPassword = findViewById(R.id.buttonSaveNewPassword1)

        buttonSaveNewPassword.setOnClickListener {
            handleChangePassword()
        }
    }

    private fun handleChangePassword() {
        // Retrieve and trim input values
        val newPassword = editTextNewPassword.text.toString().trim()
        val confirmNewPassword = editTextConfirmNewPassword.text.toString().trim()

        // Validate inputs
        if (newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty()) {
            if (newPassword == confirmNewPassword) {
                changePassword(newPassword)
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePassword(newPassword: String) {
        val user = firebaseAuth.currentUser ?: return

        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    navigateToMyPage()
                } else {
                    Toast.makeText(
                        this,
                        "비밀번호 변경 실패: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToMyPage() {
        val intent = Intent(this, MyPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()  // Close ChangeNewPasswordActivity
    }
}
