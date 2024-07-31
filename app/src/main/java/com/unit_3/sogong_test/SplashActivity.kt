package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

//class SplashActivity : AppCompatActivity() {
//    private lateinit var animationView: LottieAnimationView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//
//        // LottieAnimationView 초기화
//        animationView = findViewById(R.id.lottieAnimationView)
//
//        // 애니메이션이 끝난 후 다음 액티비티로 이동
//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 2000) // 2초 후에 다음 액티비티로 이동
//    }
//}

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

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)

    }
}