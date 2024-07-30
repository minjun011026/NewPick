package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isNicknameChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val showPasswordBtn = findViewById<ImageButton>(R.id.showPassword)
        val showRepeatPasswordBtn = findViewById<ImageButton>(R.id.showRepeatPassword)
        val checkNicknameBtn = findViewById<ImageButton>(R.id.checkNicknameBtn)

        val nicknameText = findViewById<EditText>(R.id.nameText)
        val emailText = findViewById<EditText>(R.id.emailText)
        val passwordText = findViewById<EditText>(R.id.passwordText)
        val repeatPasswordText = findViewById<EditText>(R.id.repeatPasswordText)

        val nicknameLayout = findViewById<TextInputLayout>(R.id.nicknameLayout)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val repeatPasswordLayout = findViewById<TextInputLayout>(R.id.repeatPasswordLayout)

        val nicknameMessage = findViewById<TextView>(R.id.nicknameMessage)
        val emailMessage = findViewById<TextView>(R.id.emailMessage)

        nicknameText.addTextChangedListener(createTextWatcher(nicknameLayout, nicknameMessage))
        emailText.addTextChangedListener(createTextWatcher(emailLayout, emailMessage))
        passwordText.addTextChangedListener(createTextWatcher(passwordLayout))
        repeatPasswordText.addTextChangedListener(createTextWatcher(repeatPasswordLayout))

        showPasswordBtn.setOnClickListener {
            togglePasswordVisibility(passwordText, showPasswordBtn)
        }

        showRepeatPasswordBtn.setOnClickListener {
            togglePasswordVisibility(repeatPasswordText, showRepeatPasswordBtn)
        }

        checkNicknameBtn.setOnClickListener {
            val nickname = nicknameText.text.toString().trim()
            if (nickname.isNotEmpty()) {
                checkNicknameExists(nickname) { exists ->
                    if (exists) {
                        nicknameMessage.text = "   이미 사용 중인 닉네임입니다!"
                        nicknameMessage.setTextColor(getColor(R.color.red))
                        isNicknameChecked = false
                    } else {
                        nicknameMessage.text = "   사용 가능한 닉네임입니다."
                        nicknameMessage.setTextColor(getColor(R.color.green))
                        isNicknameChecked = true
                    }
                }
            } else {
                nicknameMessage.text = "   닉네임을 입력해 주세요."
                nicknameMessage.setTextColor(getColor(R.color.black))
                isNicknameChecked = false
            }
        }

        registerBtn.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            val repeatPassword = repeatPasswordText.text.toString().trim()
            val nickname = nicknameText.text.toString().trim()

            var isValid = true

            if (nickname.isEmpty()) {
                nicknameMessage.text = "   올바른 이름을 입력해 주세요."
                nicknameMessage.setTextColor(getColor(R.color.red))
                isValid = false
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailMessage.text = "   올바른 이메일 형식이 아닙니다."
                emailMessage.setTextColor(getColor(R.color.red))
                isValid = false
            } else {
                emailMessage.text = "올바른 이메일입니다."
                emailMessage.setTextColor(getColor(R.color.green))
            }

            val passwordRegex = ".*[A-Za-z].*".toRegex()
            val numberRegex = ".*[0-9].*".toRegex()
            val specialCharRegex = ".*[@#\$%^&*()!].*".toRegex()

            if (password.isEmpty() || password.length < 8 ||
                !password.matches(passwordRegex) ||
                !password.matches(numberRegex) ||
                !password.matches(specialCharRegex)) {
                passwordLayout.error = "  숫자+영문자+특수문자 조합으로 8자리 이상 입력해 주세요."
                isValid = false
            } else {
                passwordLayout.error = null
            }

            if (repeatPassword != password) {
                repeatPasswordLayout.error = "  비밀번호가 일치하지 않습니다."
                isValid = false
            } else {
                repeatPasswordLayout.error = null
            }

            if (isValid && isNicknameChecked) {
                checkEmailExists(email) { exists ->
                    if (exists) {
                        emailMessage.text = "   이미 사용 중인 이메일입니다."
                        emailMessage.setTextColor(getColor(R.color.red))
                    } else {
                        val intent = Intent(this, TermsActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    }
                }
            }
        }
    }

    private fun createUser(email: String, password: String, nickname: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        val userData = mapOf(
                            "email" to email,
                            "nickname" to nickname
                        )
                        database.child(userId).setValue(userData)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun checkNicknameExists(nickname: String, callback: (Boolean) -> Unit) {
        database.orderByChild("nickname").equalTo(nickname).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun createTextWatcher(layout: TextInputLayout, messageView: TextView? = null): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (messageView != null) {
                    val text = s.toString()
                    if (text.isEmpty()) {
                        messageView.text = ""
                    } else if (layout.id == R.id.emailLayout) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            messageView.text = "올바른 이메일 형식이 아닙니다."
                            messageView.setTextColor(getColor(R.color.red))
                        } else {
                            messageView.text = "올바른 이메일입니다."
                            messageView.setTextColor(getColor(R.color.green))
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            button.setImageResource(R.drawable.eye)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(R.drawable.eye_off)
        }
        editText.setSelection(editText.text.length)
    }
}
