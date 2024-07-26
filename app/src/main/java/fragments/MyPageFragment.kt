package fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.unit_3.sogong_test.*
import com.unit_3.sogong_test.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Initialize UI components
        setupUI()

        // Load dark mode preference
        loadDarkModePreference()

        // Bottom navigation click listeners
        binding.bottomNavigationLocal.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_mapNewsFragment)
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
}
