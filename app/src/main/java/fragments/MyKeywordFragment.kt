package fragments

import KeywordRVAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.unit_3.sogong_test.KeywordModel
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentMyKeywordBinding


class MyKeywordFragment : Fragment() {

    private lateinit var binding: FragmentMyKeywordBinding

    private val database = Firebase.database
    val myRef = database.getReference("keyword")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_keyword, container, false)

        binding.bottomNavigationChat.setOnClickListener {
//            it.findNavController().navigate(R.id.action_myKeywordFragment_to_chatFragment)
            startActivity(Intent(context, MapViewActivity::class.java ))
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
        val rvAdapter = KeywordRVAdapter(items)
        rv.adapter = rvAdapter

        rv.layoutManager = LinearLayoutManager(requireContext())


        //데이터베이스에서 가져온 keyword들을 넣어주는 작업이 필요함.
        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for(keyword in snapshot.children){
                    try {
                        val getKeyword = keyword.getValue(KeywordModel::class.java)!!
                        getKeyword.url = keyword.key!!
                        items.add(getKeyword)
                    }catch(e:Exception){

                    }
                }
                rvAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


        return binding.root
    }

}





