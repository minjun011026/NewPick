package fragments

import android.app.appsearch.Migrator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentMyKeywordBinding


class MyKeywordFragment : Fragment() {

    private lateinit var binding : FragmentMyKeywordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_keyword, container, false)

        binding.bottomNavigationChat.setOnClickListener {
            it.findNavController().navigate(R.id.action_myKeywordFragment_to_chatFragment)
        }
        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_myKeywordFragment_to_homeFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener{
            it.findNavController().navigate(R.id.action_myKeywordFragment_to_myPageFragment)
        }

        binding.addKeywordBtn.setOnClickListener {
            val dialogFragment = AddKeywordDialogFragment()
            dialogFragment.show(childFragmentManager, "AddKeywordDialogFragment")
        }


        return binding.root

    }




}
