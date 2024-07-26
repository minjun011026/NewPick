package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.unit_3.sogong_test.FeedModel
import com.unit_3.sogong_test.FeedRVAdapter
import com.unit_3.sogong_test.FeedWriteActivity
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var database: DatabaseReference
    private lateinit var rvAdapter: FeedRVAdapter
    private val items = mutableListOf<FeedModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("feeds")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)

        binding.bottomNavigationLocal.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_mapNewsFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            it.findNavController().navigate(R.id.action_feedFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener {
            it.findNavController().navigate(R.id.action_feedFragment_to_myPageFragment)
        }

        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_feedFragment_to_homeFragment)
        }

        binding.addBtn.setOnClickListener {
            startActivity(Intent(context, FeedWriteActivity::class.java))
        }

        // Setup RecyclerView
        val rv: RecyclerView = binding.rv
        rvAdapter = FeedRVAdapter(items)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        // Load data from Firebase
        loadDataFromFirebase()

        return binding.root
    }

    private fun loadDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (dataSnapshot in snapshot.children) {
                    try {
                        val feed = dataSnapshot.getValue(FeedModel::class.java)
                        if (feed != null) {
                            items.add(feed)
                        }
                    } catch (e: Exception) {
                        Log.e("FeedFragment", "Error parsing data: ${e.message}")
                    }
                }
                // Reverse the list to show the most recent items at the top
                items.reverse()
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FeedFragment", "Database error: ${error.message}")
            }
        })
    }
}
