package com.unit_3.sogong_test


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unit_3.sogong_test.databinding.ActivityMapNewsBinding
import fragments.MapNewsFragment

class MapNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment를 로드
        loadFragment(MapNewsFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}