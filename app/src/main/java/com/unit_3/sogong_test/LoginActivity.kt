package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.unit_3.sogong_test.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // XML에서 뷰들을 찾아 변수에 할당
        val usernameEditText = findViewById<EditText>(R.id.username)
        passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerTextView = findViewById<TextView>(R.id.register)
        val showPasswordButton = findViewById<ImageButton>(R.id.showPassword)

        // 비밀번호 보기/숨기기 버튼 클릭 리스너 설정
        showPasswordButton.setOnClickListener {
            togglePasswordVisibility()
        }

        // 이메일과 비밀번호를 사용한 로그인
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "빈 칸 없이 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // FirebaseAuth를 사용하여 이메일과 비밀번호로 로그인 시도
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 시 처리
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        // 다음 화면으로 이동 등의 처리
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 로그인 실패 시 처리
                        Toast.makeText(this, "로그인 실패(비밀번호 또는 이메일을 확인해주세요.)", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // 비밀번호 보기/숨기기 기능 메서드
    private fun togglePasswordVisibility() {
        val currentInputType = passwordEditText.inputType
        if (currentInputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // 현재 보이는 상태-> 비밀변호 숨기기
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            // 현재 숨겨진 상태-> 비밀번호 보이기
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        // 커서를 맨 끝으로 이동하여 보이는 텍스트를 확인
        passwordEditText.setSelection(passwordEditText.text.length)
    }


}
