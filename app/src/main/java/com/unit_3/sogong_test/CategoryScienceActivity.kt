package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.unit_3.sogong_test.databinding.ActivityCategoryScienceBinding

class CategoryScienceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoryScienceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_science)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_science)

        binding.backBtn.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))

        }
    }
}