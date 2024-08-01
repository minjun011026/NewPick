package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val allCheckbox = findViewById<CheckBox>(R.id.allCheckbox)
        val ageCheckbox = findViewById<CheckBox>(R.id.ageCheckbox)
        val termsCheckbox = findViewById<CheckBox>(R.id.termsCheckbox)
        val privacyCheckbox = findViewById<CheckBox>(R.id.privacyCheckbox)
        val adsCheckbox = findViewById<CheckBox>(R.id.adsCheckbox)
        val acceptTermsBtn = findViewById<Button>(R.id.acceptTermsBtn)

        allCheckbox.setOnCheckedChangeListener { _, isChecked ->
            ageCheckbox.isChecked = isChecked
            termsCheckbox.isChecked = isChecked
            privacyCheckbox.isChecked = isChecked
            adsCheckbox.isChecked = isChecked
        }

        acceptTermsBtn.setOnClickListener {
            if (ageCheckbox.isChecked && termsCheckbox.isChecked && privacyCheckbox.isChecked) {
                // 동의한 경우, 로그인 액티비티로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()  // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "필수 항목에 동의해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
