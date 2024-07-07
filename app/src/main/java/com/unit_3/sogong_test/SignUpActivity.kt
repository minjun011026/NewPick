package com.unit_3.sogong_test

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth

        val registerBtn =findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailText)
            val password = findViewById<EditText>(R.id.passwordText)
            val repeatPassword = findViewById<EditText>(R.id.repeatPasswordText)

            if(email.text != null && password.text != null){
                if(password.text.toString() != repeatPassword.text.toString())
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                else{
                    auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(
                                    baseContext,
                                    "회원가입 완료",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    baseContext,
                                    "사용할 수 없는 이메일/비밀번호 형식입니다.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }
        }
    }

}