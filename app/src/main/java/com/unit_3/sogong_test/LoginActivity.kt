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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var passwordEditText: EditText
    private lateinit var showPasswordButton: ImageButton
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        // XML에서 뷰들을 찾아 변수에 할당
        val usernameEditText = findViewById<EditText>(R.id.username)
        passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordBtn = findViewById<TextView>(R.id.forgotPasswordBtn)
        val registerBtn = findViewById<TextView>(R.id.registerBtn)
        showPasswordButton = findViewById(R.id.showPassword)
        val googleSignInButton = findViewById<Button>(R.id.googleSignInButton)

        // 비밀번호 보기/숨기기 버튼 클릭 리스너 설정
        showPasswordButton.setOnClickListener {
            togglePasswordVisibility()
        }

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener {
            val input = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "빈 칸 없이 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (input.contains("@")) {
                // 이메일로 로그인 시도
                signInWithEmail(input, password)
            } else {
                // 닉네임으로 이메일 찾기 후 로그인 시도
                signInWithNickname(input, password)
            }
        }

        // 구글 로그인 버튼 클릭 리스너 설정
        googleSignInButton.setOnClickListener {
            // 구글 로그인 로직 추가
            // ...
        }

        forgotPasswordBtn.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    // 비밀번호 보기/숨기기 기능 메서드
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // 현재 보이는 상태 -> 비밀번호 숨기기
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            showPasswordButton.setImageResource(R.drawable.eye) // 눈 감김 아이콘
        } else {
            // 현재 숨겨진 상태 -> 비밀번호 보이기
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            showPasswordButton.setImageResource(R.drawable.eye_off) // 눈 뜸 아이콘
        }
        isPasswordVisible = !isPasswordVisible
        // 커서를 맨 끝으로 이동하여 보이는 텍스트를 확인
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    // 이메일로 로그인 시도
    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "로그인 실패(비밀번호 또는 이메일을 확인해주세요.)", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 닉네임으로 이메일 찾기 후 로그인 시도
    private fun signInWithNickname(nickname: String, password: String) {
        database.orderByChild("nickname").equalTo(nickname)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 닉네임에 해당하는 이메일 찾기
                        val email = snapshot.children.first().child("email").getValue(String::class.java)
                        if (email != null) {
                            signInWithEmail(email, password)
                        } else {
                            Toast.makeText(this@LoginActivity, "이메일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "해당 닉네임을 가진 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "데이터베이스 오류: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
