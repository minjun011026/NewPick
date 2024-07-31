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
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val appVersion = packageInfo.versionName
            val deviceAppVersion = android.os.Build.VERSION.RELEASE

            binding.textViewAppVersion.text = "앱 버전: $appVersion"
            binding.textViewDeviceAppVersion.text = "기기 앱 버전: $deviceAppVersion"

            // Check if the app version is the latest
            val latestAppVersion = getLatestAppVersionFromServer() // You need to implement this method

            if (appVersion != latestAppVersion) {
                binding.textViewUpdateMessage.text = "현재 버전이 낮습니다! 업데이트를 해주세요!"
            } else {
                binding.textViewUpdateMessage.text = "현재 가장 최신 버전입니다!"
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getLatestAppVersionFromServer(): String {
        // This is a placeholder for the actual implementation
        // You should replace this with a network call or a method to get the latest app version from your server or database
        return "1.0.0" // Example version
    }
}
