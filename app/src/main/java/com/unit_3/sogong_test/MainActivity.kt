package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fragments.FeedFragment
import fragments.HomeFragment
import fragments.MapNewsFragment
import fragments.MyKeywordFragment
import fragments.MyPageFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

//    private fun handleIntent(intent: Intent?) {
//        if (intent != null) {
//            val fromActivity = intent.getStringExtra("fromActivity")
//            if (fromActivity == "MapViewActivity") {
//                replaceFragment(MapNewsFragment())
//            } else {
//                val fragmentName = intent.getStringExtra("fragmentName")
//                if (fragmentName != null) {
//                    when (fragmentName) {
//                        "HomeFragment" -> replaceFragment(HomeFragment())
//                        "MyKeywordFragment" -> replaceFragment(MyKeywordFragment())
//                        "MyPageFragment" -> replaceFragment(MyPageFragment())
//                        "FeedFragment" -> replaceFragment(FeedFragment())
//                    }
//                }
//            }
//        }
//    }

    private fun handleIntent(intent: Intent?) {
        if (intent != null) {
            val fromActivity = intent.getStringExtra("fromActivity")
            if (fromActivity == "MapViewActivity") {
                replaceFragment(MapNewsFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
