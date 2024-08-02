package fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import com.unit_3.sogong_test.*
import com.unit_3.sogong_test.databinding.FragmentMyPageBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.Locale

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: CircleImageView
    private var imageUri: Uri? = null
    private val defaultImageUrl = "URL_OF_DEFAULT_IMAGE"
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        imageView = binding.ivProfile
        auth = FirebaseAuth.getInstance()

        // Load dark mode preference
        val isDarkMode = sharedPreferences.getBoolean("darkMode", false)
        setDarkMode(isDarkMode)

        // Refresh UI with current locale
        refreshUI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch user info from Firebase
        fetchUserInfoFromFirebase()

        // 프로필 이미지 클릭 리스너
        imageView.setOnClickListener {
            showImagePickerDialog()
        }

        // 이미지 변경 버튼 클릭 리스너
        binding.buttonChangeProfileImage.setOnClickListener {
            showImagePickerDialog()
        }

        // Bottom navigation click listeners
        binding.bottomNavigationLocal.setOnClickListener {
            checkUserLocation()
        }
        binding.bottomNavigationHome.setOnClickListener {
            view.findNavController().navigate(R.id.action_myPageFragment_to_homeFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            view.findNavController().navigate(R.id.action_myPageFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationFeed.setOnClickListener {
            view.findNavController().navigate(R.id.action_myPageFragment_to_feedFragment)
        }

        // Handle click on "내가 북마크한 글"
        binding.bookmarkedNewsTextView.setOnClickListener {
            openBookmarkedNewsActivity()
        }

        // Handle click on "최근 본 아티클"
        binding.recentArticlesTextView.setOnClickListener {
            openRecentArticlesActivity()
        }

        // 닉네임 변경 버튼 클릭 리스너 추가
        binding.buttonChangeNickname.setOnClickListener {
            val intent = Intent(context, ChangeNicknameActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CHANGE_NICKNAME)
        }

        // 비밀번호 변경 버튼 클릭 리스너 추가
        binding.buttonChangePassword.setOnClickListener {
            val intent = Intent(context, VerifyCurrentPasswordActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 리스너 추가
        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // 회원 탈퇴 버튼 클릭 리스너 추가
        binding.buttonAccountDeletion.setOnClickListener {
            showAccountDeletionConfirmationDialog()
        }

        // 앱 버전 버튼 클릭 리스너 추가
        binding.version.setOnClickListener {
            val intent = Intent(context, AppVersionActivity::class.java)
            startActivity(intent)
        }

        // 공지사항 버튼 클릭 리스너 추가
        binding.gongji.setOnClickListener {
            val intent = Intent(context, NoticeActivity::class.java)
            startActivity(intent)
        }

        // 언어 설정 버튼 클릭 리스너 추가
        binding.language.setOnClickListener {
            showLanguageSelectionDialog()
        }

        // Clear Cache button click listener
        binding.buttonClearCache.setOnClickListener {
            showClearCacheConfirmationDialog()
        }
    }

    private fun setDarkMode(enabled: Boolean) {
        if (isAdded) {
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            with(sharedPreferences.edit()) {
                putBoolean("darkMode", enabled)
                apply()
            }
        }
    }

    private fun openBookmarkedNewsActivity() {
        val intent = Intent(context, BookmarkedNewsActivity::class.java)
        startActivity(intent)
    }

    private fun openRecentArticlesActivity() {
        val intent = Intent(context, RecentArticlesActivity::class.java)
        startActivity(intent)
    }

    private fun refreshUI() {
        binding.textViewUserLocation.text = getString(R.string.user_location) // 예시로 사용
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            imageUri = data.data
            imageUri?.let { uri ->
                imageView.setImageURI(uri)
                uploadImageToFirebase(uri)
            }
        } else if (requestCode == REQUEST_CODE_CHANGE_NICKNAME && resultCode == Activity.RESULT_OK) {
            val newNickname = data?.getStringExtra("nickname")
            newNickname?.let {
                binding.nicknameTextView.text = it
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val user = auth.currentUser ?: return
        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("uploads/${user.uid}/${System.currentTimeMillis()}.jpg")

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUriToDatabase(uri.toString())
                }
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
            }
    }

    private fun saveImageUriToDatabase(downloadUri: String) {
        val user = auth.currentUser ?: return
        val database = Firebase.database
        val reference = database.getReference("users").child(user.uid).child("profile_picture")
        reference.setValue(downloadUri)
    }

    private fun setDefaultImage() {
        imageView.setImageResource(R.drawable.account_circle) // 기본 이미지 리소스 설정
        saveImageUriToDatabase(defaultImageUrl)
    }

    private fun loadImageFromDatabase(imageUrl: String) {
        if (imageUrl != defaultImageUrl) {
            Glide.with(requireActivity()).load(imageUrl).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.account_circle)
        }
    }

    private fun fetchUserInfoFromFirebase() {
        val user = auth.currentUser ?: return
        val database = Firebase.database
        val userRef = database.getReference("users").child(user.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.child("nickname").getValue(String::class.java) ?: "기본 닉네임"
                val email = snapshot.child("email").getValue(String::class.java) ?: "기본 이메일"
                val profilePictureUrl = snapshot.child("profile_picture").getValue(String::class.java) ?: defaultImageUrl

                // 위치 정보 가져오기
                val locationRef = database.getReference("location").child(user.uid)
                locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(locationSnapshot: DataSnapshot) {
                        val locations = locationSnapshot.children.mapNotNull { it.getValue(String::class.java) }
                        val userLocation = if (locations.isNotEmpty()) locations[0] else "위치 정보 없음"

                        // Update SharedPreferences
                        with(sharedPreferences.edit()) {
                            putString("nickname", nickname)
                            putString("email", email)
                            putString("profile_picture", profilePictureUrl)
                            putString("user_location", userLocation)
                            apply()
                        }

                        // Update UI
                        if (isAdded) {
                            binding.nicknameTextView.text = nickname
                            binding.emailTextView.text = email
                            binding.textViewUserLocation.text = "내 위치: $userLocation"
                            loadImageFromDatabase(profilePictureUrl)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MyPageFragment", "Location database error: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyPageFragment", "User database error: ${error.message}")
            }
        })
    }


    private fun checkUserLocation() {
        val currentUserId = Firebase.auth.currentUser?.uid
        currentUserId?.let {
            val locationRef = Firebase.database.getReference("location").child(it)
            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded) {
                        if (snapshot.exists() && snapshot.childrenCount > 0) {
                            // Location exists, navigate to MapNewsFragment
                            view?.findNavController()?.navigate(R.id.action_myPageFragment_to_mapNewsFragment)
                        } else {
                            // No location, navigate to MapViewActivity
                            val intent = Intent(context, MapViewActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyPageFragment", "Database error: ${error.message}")
                }
            })
        }
    }

    private fun showImagePickerDialog() {
        if (isAdded) {
            val options = arrayOf("갤러리에서 사진 가져오기", "기본 이미지로 변경", "취소")
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("프로필 사진 변경")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> openFileChooser()
                    1 -> setDefaultImage()
                    2 -> dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun showLanguageSelectionDialog() {
        if (isAdded) {
            val languages = arrayOf("한국어", "영어", "중국어", "일본어")
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("언어 설정")
            builder.setItems(languages) { dialog, which ->
                when (which) {
                    0 -> setLocale("ko")
                    1 -> setLocale("en")
                    2 -> setLocale("zh")
                    3 -> setLocale("ja")
                }
            }
            builder.show()
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Recreate the activity to apply the new locale settings
        activity?.recreate()
    }


    private fun showLogoutConfirmationDialog() {
        if (isAdded) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("로그아웃")
            builder.setMessage("정말로 로그아웃하시겠습니까?")
            builder.setPositiveButton("예") { dialog, _ ->
                firebaseAuth.signOut()
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            builder.setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun showAccountDeletionConfirmationDialog() {
        if (isAdded) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("회원 탈퇴")
            builder.setMessage("정말로 회원 탈퇴하시겠습니까?")
            builder.setPositiveButton("예") { dialog, _ ->
                val user = firebaseAuth.currentUser
                user?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        // Handle failure
                    }
                }
            }
            builder.setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun showClearCacheConfirmationDialog() {
        if (isAdded) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("캐시 삭제")
            builder.setMessage("정말로 캐시를 삭제하시겠습니까?")
            builder.setPositiveButton("예") { _, _ ->
                clearCache()
            }
            builder.setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun clearCache() {
        try {
            val cacheDir = requireContext().cacheDir
            deleteDir(cacheDir)
            Toast.makeText(context, "캐시가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "캐시 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            Log.e("MyPageFragment", "Failed to clear cache", e)
        }
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.listFiles()
            if (children != null) {
                for (child in children) {
                    if (!deleteDir(child)) {
                        return false
                    }
                }
            }
        }
        return dir.delete()
    }


    companion object {
        private const val REQUEST_CODE_CHANGE_NICKNAME = 2
    }
}
