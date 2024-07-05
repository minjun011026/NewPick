package com.unit_3.sogong_test

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.unit_3.sogong_test.databinding.ActivityCategoryPoliticsBinding
import fragments.HomeFragment

class CategoryPoliticsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoryPoliticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_politics)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_politics)

        binding.backBtn.setOnClickListener {

        }

    }
}