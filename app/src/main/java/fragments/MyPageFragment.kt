package fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.unit_3.sogong_test.BookmarkedNewsActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)

        // Bottom navigation click listeners
        binding.bottomNavigationChat.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_chatFragment)
        }
        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_homeFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_myKeywordFragment)
        }


        // Handle click on "내가 북마크한 글"
        binding.bookmarkedNewsTextView.setOnClickListener {
            openBookmarkedNewsActivity()
        }

        return binding.root
    }

    private fun openBookmarkedNewsActivity() {
        val intent = Intent(requireContext(), BookmarkedNewsActivity::class.java)
        startActivity(intent)
    }
}
