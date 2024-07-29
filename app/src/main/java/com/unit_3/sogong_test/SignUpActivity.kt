package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        nicknameText.addTextChangedListener(createTextWatcher(findViewById(R.id.nicknameLayout), findViewById(R.id.nicknameMessage)))
//        emailText.addTextChangedListener(createTextWatcher(findViewById(R.id.emailLayout), findViewById(R.id.emailMessage)))
//        passwordText.addTextChangedListener(createTextWatcher(findViewById(R.id.passwordLayout), findViewById(R.id.passwordMessage)))
//        repeatPasswordText.addTextChangedListener(createTextWatcher(findViewById(R.id.repeatPasswordLayout), findViewById(R.id.repeatPasswordMessage)))

        showPasswordBtn.setOnClickListener {
            togglePasswordVisibility(passwordText, showPasswordBtn)
        }

        showRepeatPasswordBtn.setOnClickListener {
            togglePasswordVisibility(repeatPasswordText, showRepeatPasswordBtn)
        }

        checkNicknameBtn.setOnClickListener {
            val nickname = nicknameText.text.toString().trim()
            checkNickname(nickname)
        }

        registerBtn.setOnClickListener {
            val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
            val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
            val repeatPasswordLayout = findViewById<TextInputLayout>(R.id.repeatPasswordLayout)
            val nicknameLayout = findViewById<TextInputLayout>(R.id.nicknameLayout)

            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            val repeatPassword = repeatPasswordText.text.toString().trim()
            val nickname = nicknameText.text.toString().trim()

            var isValid = true

            if (nickname.isEmpty()) {
                nicknameLayout.error = "올바른 이름을 입력해 주세요."
                isValid = false
            } else {
                nicknameLayout.error = null
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "올바른 이메일 형식이 아닙니다."
                isValid = false
            } else {
                emailLayout.error = null
            }

            if (password.isEmpty() || password.length < 8 || !password.matches(".*[A-Za-z].*".toRegex()) || !password.matches(".*[0-9].*".toRegex()) || !password.matches(".*[@#\$%^&+=].*".toRegex())) {
                passwordLayout.error = "숫자+영문자+특수문자 조합으로 8자리 이상 입력해 주세요."
                isValid = false
            } else {
                passwordLayout.error = null
            }

            if (repeatPassword != password) {
                repeatPasswordLayout.error = "비밀번호가 일치하지 않습니다."
                isValid = false
            } else {
                repeatPasswordLayout.error = null
            }

            if (isValid) {
                checkEmailExists(email) { exists ->
                    if (exists) {
                        emailLayout.error = "이미 사용 중인 이메일입니다."
                    } else {
                        createUser(email, password, nickname)
                    }
                }
            }
        }
    }

    private fun createTextWatcher(layout: TextInputLayout, messageView: TextView): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateInput(layout, messageView)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun validateInput(layout: TextInputLayout, messageView: TextView) {
        val input = layout.editText?.text.toString().trim()
        when (layout.id) {
            R.id.nicknameLayout -> {
                if (input.isEmpty()) {
                    setErrorMessage(layout, messageView, "올바른 이름을 입력해 주세요.", false)
                } else {
                    setErrorMessage(layout, messageView, null, true)
                }
            }
            R.id.emailLayout -> {
                if (input.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    setErrorMessage(layout, messageView, "올바른 이메일 형식이 아닙니다.", false)
                } else {
                    setErrorMessage(layout, messageView, null, true)
                }
            }
            R.id.passwordLayout, R.id.repeatPasswordLayout -> {
                val password = layout.editText?.text.toString().trim()
                if (password.isEmpty() || password.length < 8 || !password.matches(".*[A-Za-z].*".toRegex()) || !password.matches(".*[0-9].*".toRegex()) || !password.matches(".*[@#\$%^&+=].*".toRegex())) {
                    setErrorMessage(layout, messageView, "숫자+영문자+특수문자 조합으로 8자리 이상 입력해 주세요.", false)
                } else {
                    setErrorMessage(layout, messageView, null, true)
                }
            }
        }
    }

    private fun setErrorMessage(layout: TextInputLayout, messageView: TextView, message: String?, isValid: Boolean) {
        if (isValid) {
            layout.error = null
            layout.boxStrokeColor = ContextCompat.getColor(this, R.color.green)
            messageView.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            layout.error = message
            layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
            messageView.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
        messageView.text = message
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        if (editText.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            button.setImageResource(R.drawable.eye)
        } else {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(R.drawable.eye_off)
        }
        editText.setSelection(editText.text.length)
    }

    private fun checkNickname(nickname: String) {
        val nicknameLayout = findViewById<TextInputLayout>(R.id.nicknameLayout)
        val nicknameMessage = findViewById<TextView>(R.id.nicknameMessage)
        if (nickname.isEmpty()) {
            setErrorMessage(nicknameLayout, nicknameMessage, "닉네임을 입력해 주세요.", false)
            return
        }
        database.orderByChild("nickname").equalTo(nickname)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        setErrorMessage(nicknameLayout, nicknameMessage, "이미 사용 중인 닉네임입니다.", false)
                    } else {
                        setErrorMessage(nicknameLayout, nicknameMessage, "사용 가능한 닉네임입니다.", true)
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
