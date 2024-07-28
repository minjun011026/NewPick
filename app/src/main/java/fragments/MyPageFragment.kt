package fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.Circle
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.storage
import com.unit_3.sogong_test.*
import com.unit_3.sogong_test.databinding.FragmentMyPageBinding
import de.hdodenhof.circleimageview.CircleImageView

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
        imageView.setOnClickListener {
            showImagePickerDialog()
        }
        //이미지 가져오기
        loadImageFromDatabase()
        // Initialize UI components
        setupUI()

        // Load dark mode preference
        loadDarkModePreference()

        // Bottom navigation click listeners
        binding.bottomNavigationLocal.setOnClickListener {
            checkUserLocation()
        }
        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_homeFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationFeed.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_feedFragment)
        }

        // Handle click on "내가 북마크한 글"
        binding.bookmarkedNewsTextView.setOnClickListener {
            openBookmarkedNewsActivity()
        }

        binding.myFeedTextView.setOnClickListener {
            // Your code to handle "내가 작성한 글" click
        }

        // 닉네임 변경 버튼 클릭 리스너 추가
        binding.buttonChangeNickname.setOnClickListener {
            val intent = Intent(requireContext(), ChangeNicknameActivity::class.java)
            startActivity(intent)
        }

        // 이메일 변경 버튼 클릭 리스너 추가
        binding.buttonChangeEmail.setOnClickListener {
            val intent = Intent(requireContext(), ChangeEmailActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 변경 버튼 클릭 리스너 추가
        binding.buttonChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), VerifyCurrentPasswordActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 리스너 추가
        binding.buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // Close MyPageActivity
        }

        // 회원 탈퇴 버튼 클릭 리스너 추가
        binding.buttonAccountDeletion.setOnClickListener {
            val user = firebaseAuth.currentUser
            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()  // Close MyPageActivity
                } else {
                    // Handle failure
                }
            }
        }

        // 다크 모드 스위치 리스너 추가
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
        }

        // SharedPreferences에서 닉네임과 이메일 불러오기
        val nickname = sharedPreferences.getString("nickname", "기본 닉네임")
        val email = sharedPreferences.getString("email", "기본 이메일")

        binding.nicknameTextView.text = nickname
        binding.emailTextView.text = email

        return binding.root
    }

    private fun setupUI() {
        // Initialize any additional UI components if needed
    }

    private fun loadDarkModePreference() {
        val isDarkMode = sharedPreferences.getBoolean("darkMode", false)
        binding.switchDarkMode.isChecked = isDarkMode
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setDarkMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        with(sharedPreferences.edit()) {
            putBoolean("darkMode", enabled)
            apply()
        }
    }

    private fun openBookmarkedNewsActivity() {
        val intent = Intent(requireContext(), BookmarkedNewsActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // 닉네임과 이메일이 변경되었을 경우를 대비해 onResume에서 업데이트
        val nickname = sharedPreferences.getString("nickname", "기본 닉네임")
        val email = sharedPreferences.getString("email", "기본 이메일")
        binding.nicknameTextView.text = nickname
        binding.emailTextView.text = email
    }

    private fun showImagePickerDialog() {
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

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
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
        imageView.setImageResource(R.drawable.default_image) // 기본 이미지 리소스 설정
        saveImageUriToDatabase(defaultImageUrl)
    }
    private fun loadImageFromDatabase() {
        val user = auth.currentUser ?: return
        val database = Firebase.database
        val reference = database.getReference("users").child(user.uid).child("profile_picture")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty() && imageUrl != defaultImageUrl) {
                    Glide.with(requireActivity()).load(imageUrl).into(imageView)
                } else {
                    imageView.setImageResource(R.drawable.default_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun checkUserLocation() {
        val currentUserId = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
        currentUserId?.let {
            val locationRef = com.google.firebase.ktx.Firebase.database.getReference("location").child(it)
            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        // Location exists, navigate to MapNewsFragment
                        view?.findNavController()?.navigate(R.id.action_myPageFragment_to_mapNewsFragment)
                    } else {
                        // No location, navigate to MapViewActivity
                        val intent = Intent(requireContext(), MapViewActivity::class.java)
//                        intent.putExtra("from", "myPage")
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyKeywordFragment", "Database error: ${error.message}")
                }
            })
        }
    }
}