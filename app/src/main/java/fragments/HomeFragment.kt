package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
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
