package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isNicknameChecked = false
    private var isPasswordVisible = false
    private var isRepeatPasswordVisible = false

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Update UI if user is already signed in
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val showEqualsBtn = findViewById<ImageButton>(R.id.showequals)
        val showPasswordButton = findViewById<ImageButton>(R.id.showPassword)
        val showRepeatPasswordButton = findViewById<ImageButton>(R.id.showRepeatPassword)
        val passwordEditText = findViewById<EditText>(R.id.passwordText)
        val repeatPasswordEditText = findViewById<EditText>(R.id.repeatPasswordText)

        showEqualsBtn.setOnClickListener {
            checkNicknameAvailability()
        }

        showPasswordButton.setOnClickListener {
            togglePasswordVisibility(passwordEditText, showPasswordButton)
        }

        showRepeatPasswordButton.setOnClickListener {
            togglePasswordVisibility(repeatPasswordEditText, showRepeatPasswordButton)
        }

        registerBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailText)
            val password = findViewById<EditText>(R.id.passwordText)
            val repeatPassword = findViewById<EditText>(R.id.repeatPasswordText)
            val nickname = findViewById<EditText>(R.id.nameText)

            if (email.text.isNotEmpty() && password.text.isNotEmpty() && nickname.text.isNotEmpty()) {
                if (password.text.toString() != repeatPassword.text.toString())
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                else if (!isNicknameChecked) {
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_LONG).show()
                } else {
                    checkEmailExists(email.text.toString()) { exists ->
                        if (exists) {
                            Toast.makeText(this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            createUser(email.text.toString(), password.text.toString(), nickname.text.toString())
                        }
                    }
                }
            } else {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 비밀번호 보기/숨기기 기능 메서드
    private fun togglePasswordVisibility(passwordEditText: EditText, showPasswordButton: ImageButton) {
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

    private fun checkNicknameAvailability() {
        val nickname = findViewById<EditText>(R.id.nameText).text.toString().trim()
        if (nickname.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        database.orderByChild("nickname").equalTo(nickname)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(this@SignUpActivity, "이미 해당 닉네임이 존재합니다!", Toast.LENGTH_SHORT).show()
                        isNicknameChecked = false
                    } else {
                        Toast.makeText(this@SignUpActivity, "사용 가능한 닉네임입니다!", Toast.LENGTH_SHORT).show()
                        isNicknameChecked = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SignUpActivity, "데이터베이스 오류: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SignUpActivity, "데이터베이스 오류: ${error.message}", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            })
    }

    private fun createUser(email: String, password: String, nickname: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = User(nickname, email, userId ?: "", password)
                    userId?.let {
                        database.child(it).setValue(user)
                    }

                    // SharedPreferences에 닉네임과 이메일 저장
                    val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("nickname", nickname)
                    editor.putString("email", email)
                    editor.apply()

                    Toast.makeText(baseContext, "회원가입 완료", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "사용할 수 없는 이메일/비밀번호 형식입니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    data class User(val nickname: String, val email: String, val uid: String, val password: String)
}
