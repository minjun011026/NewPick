package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unit_3.sogong_test.KeywordModel
import com.unit_3.sogong_test.KeywordRVAdapter
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentMyKeywordBinding


class MyKeywordFragment : Fragment() {

    private lateinit var binding: FragmentMyKeywordBinding


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
        binding.bottomNavigationMyPage.setOnClickListener {
            it.findNavController().navigate(R.id.action_myKeywordFragment_to_myPageFragment)
        }

        binding.addKeywordBtn.setOnClickListener {
            val dialogFragment = AddKeywordDialogFragment()
            dialogFragment.show(childFragmentManager, "AddKeywordDialogFragment")
        }


        val rv: RecyclerView = binding.rv

        val items = ArrayList<KeywordModel>()

        //데이터베이스에서 가져온 keyword들을 넣어주는 작업이 필요함.


        items.add(KeywordModel("손흥민"))
        items.add(KeywordModel("여중대장"))
        items.add(KeywordModel("연준 금리"))

        val rvAdapter = KeywordRVAdapter(items)
        rv.adapter = rvAdapter

        rv.layoutManager = LinearLayoutManager(requireContext())




        return binding.root
//
//        // RecyclerView 설정
//        val rvAdapter = KeywordRVAdapter(getDummyData())
//        binding.rv.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = rvAdapter
//        }
//
//        return binding.root
//    }
//
//    private fun getDummyData(): ArrayList<KeywordModel> {
//        return arrayListOf(
//            KeywordModel("손흥민"),
//            KeywordModel("여중대장"),
//            KeywordModel("연준 금리")
//        )
//    }

    }
}





