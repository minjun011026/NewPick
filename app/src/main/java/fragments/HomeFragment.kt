package fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.unit_3.sogong_test.CategoryEconomyActivity
import com.unit_3.sogong_test.CategoryEntertainmentActivity
import com.unit_3.sogong_test.CategoryGlobalActivity
import com.unit_3.sogong_test.CategoryLifeActivity
import com.unit_3.sogong_test.CategoryPoliticsActivity
import com.unit_3.sogong_test.CategoryScienceActivity
import com.unit_3.sogong_test.CategorySocietyActivity
import com.unit_3.sogong_test.CategorySportsActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.categoryPoliticsBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryPoliticsActivity::class.java))
        }
        binding.categoryEconomyBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryEconomyActivity::class.java))
        }
        binding.categorySocietyBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategorySocietyActivity::class.java))
        }
        binding.categoryLifeBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryLifeActivity::class.java))
        }
        binding.categorySportsBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategorySportsActivity::class.java))
        }
        binding.categoryEntertainmentBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryEntertainmentActivity::class.java))
        }
        binding.categoryScienceBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryScienceActivity::class.java))
        }
        binding.categoryGlobalBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryGlobalActivity::class.java))
        }


        binding.bottomNavigationChat.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
           it.findNavController().navigate(R.id.action_homeFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        return binding.root


    }


}
