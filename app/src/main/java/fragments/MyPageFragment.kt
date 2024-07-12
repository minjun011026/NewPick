package fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentMyPageBinding


class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)

        binding.bottomNavigationChat.setOnClickListener {
//            it.findNavController().navigate(R.id.action_myPageFragment_to_chatFragment)
            startActivity(Intent(context, MapViewActivity::class.java ))
        }
        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPageFragment_to_homeFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener{
            it.findNavController().navigate(R.id.action_myPageFragment_to_myKeywordFragment)
        }

        return binding.root


    }


}
