package com.unit_3.sogong_test

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unit_3.sogong_test.databinding.ActivityAppVersionBinding

class AppVersionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppVersionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Setting both app version and device version to 2.0.0
            val appVersion = "2.0.0"
            val deviceAppVersion = "2.0.0" // You might want to set a consistent version

            binding.textViewAppVersion.text = "앱 버전: $appVersion"
            binding.textViewDeviceAppVersion.text = "기기 앱 버전: $deviceAppVersion"

            // Check if the app version is the latest
            val latestAppVersion = getLatestAppVersionFromServer()

            if (appVersion != latestAppVersion) {
                binding.textViewUpdateMessage.text = "현재 버전이 낮습니다!"
            } else {
                binding.textViewUpdateMessage.text = "현재 가장 최신 버전입니다!"
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getLatestAppVersionFromServer(): String {
        // This is a placeholder for the actual implementation
        // Return "2.0.0" to match the version set above
        return "2.0.0"
    }
}
