package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.unit_3.sogong_test.databinding.ActivityCategoryLifeBinding

class CategoryLifeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoryLifeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_life)

        binding = setContentView(this, R.layout.activity_category_life)

        binding.backBtn.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))

        }


    }
}