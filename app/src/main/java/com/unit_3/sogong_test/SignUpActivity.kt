package com.unit_3.sogong_test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
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
    private val TERMS_REQUEST_CODE = 1

    // Declare view variables at the class level
    private lateinit var nicknameText: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var repeatPasswordText: EditText

    private lateinit var nicknameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var repeatPasswordLayout: TextInputLayout

    private lateinit var nicknameMessage: TextView
    private lateinit var emailMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Initialize view variables
        nicknameText = findViewById(R.id.nameText)
        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passwordText)
        repeatPasswordText = findViewById(R.id.repeatPasswordText)

        nicknameLayout = findViewById(R.id.nicknameLayout)
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        repeatPasswordLayout = findViewById(R.id.repeatPasswordLayout)

        nicknameMessage = findViewById(R.id.nicknameMessage)
        emailMessage = findViewById(R.id.emailMessage)

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val showPasswordBtn = findViewById<ImageButton>(R.id.showPassword)
        val showRepeatPasswordBtn = findViewById<ImageButton>(R.id.showRepeatPassword)
        val checkNicknameBtn = findViewById<ImageButton>(R.id.checkNicknameBtn)

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
                        nicknameMessage.text = "  이미 사용 중인 닉네임입니다!"
                        nicknameMessage.setTextColor(getColor(R.color.red))
                        isNicknameChecked = false
                    } else {
                        nicknameMessage.text = "  사용 가능한 닉네임입니다."
                        nicknameMessage.setTextColor(getColor(R.color.blue))
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
                        emailMessage.text = "  이미 사용 중인 이메일입니다."
                        emailMessage.setTextColor(getColor(R.color.red))
                    } else {
                        val intent = Intent(this, TermsActivity::class.java)
                        startActivityForResult(intent, TERMS_REQUEST_CODE)
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
                                    Log.d("SignUp", "회원가입이 완료되었습니다.")
                                    Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()  // 회원가입 완료 문구 추가
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.e("SignUp", "회원가입에 실패했습니다.", dbTask.exception)
                                    Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Log.e("SignUp", "회원가입에 실패했습니다.", task.exception)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TERMS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val isAgreed = data?.getBooleanExtra("isAgreed", false) ?: false
            if (isAgreed) {
                val email = emailText.text.toString().trim()
                val password = passwordText.text.toString().trim()
                val nickname = nicknameText.text.toString().trim()
                createUser(email, password, nickname)
            } else {
                Toast.makeText(this, "동의하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
