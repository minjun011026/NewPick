package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView


class SplashActivity : AppCompatActivity() {
    private lateinit var animationView: LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_splash)
        try {
            animationView = findViewById(R.id.lottieAnimationView)
        } catch (e: Exception) {
            e.printStackTrace() // 로그 출력
        }

        // Fade In 애니메이션 적용
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val textView = findViewById<TextView>(R.id.textViewCustomNews) // findViewById로 TextView 가져오기
        textView.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}