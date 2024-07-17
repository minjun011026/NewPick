package fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentFeedBinding


class FeedFragment : Fragment() {
    private lateinit var binding : FragmentFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)

        binding.bottomNavigationChat.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
            startActivity(Intent(context, MapViewActivity::class.java))
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            it.findNavController().navigate(R.id.action_feedFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener{
            it.findNavController().navigate(R.id.action_feedFragment_to_myPageFragment)
        }

        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_feedFragment_to_homeFragment)
        }

        // RecyclerView 초기화
        binding.rv.layoutManager = LinearLayoutManager(requireContext())


        return binding.root
    }


}