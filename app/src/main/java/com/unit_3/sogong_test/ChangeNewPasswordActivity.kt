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
        setContentView(R.layout.activity_change_password) // 여기서 activity_change_password.xml 파일을 참조합니다

        firebaseAuth = FirebaseAuth.getInstance()

        editTextNewPassword = findViewById(R.id.editTextNewPassword1)
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword1)
        buttonSaveNewPassword = findViewById(R.id.buttonSaveNewPassword1)

        buttonSaveNewPassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString()
            val confirmNewPassword = editTextConfirmNewPassword.text.toString()

            if (newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty()) {
                if (newPassword == confirmNewPassword) {
                    firebaseAuth.currentUser?.updatePassword(newPassword)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                // 비밀번호 변경 후 MyPageActivity로 이동
                                val intent = Intent(this, MyPageActivity::class.java)
                                // Set flags to clear the task stack
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()  // Close ChangeNewPasswordActivity
                            } else {
                                Toast.makeText(
                                    this,
                                    "비밀번호 변경 실패: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
