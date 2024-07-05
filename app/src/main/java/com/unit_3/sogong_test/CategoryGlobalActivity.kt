package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.unit_3.sogong_test.databinding.ActivityCategoryGlobalBinding

class CategoryGlobalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoryGlobalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_global)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_global)

        binding.backBtn.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))

        }

    }
}