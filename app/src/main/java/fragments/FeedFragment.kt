package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.unit_3.sogong_test.FeedModel
import com.unit_3.sogong_test.FeedRVAdapter
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var database: DatabaseReference
    private lateinit var rvAdapter: FeedRVAdapter
    private val items = mutableListOf<FeedModel>()
    private val filteredItems = mutableListOf<FeedModel>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("feeds")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)

        binding.bottomNavigationLocal.setOnClickListener {
//            it.findNavController().navigate(R.id.action_feedFragment_to_mapNewsFragment)
            checkUserLocation()
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

        // Setup RecyclerView
        val rv: RecyclerView = binding.rv
        rvAdapter = FeedRVAdapter(filteredItems)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        // Setup Spinner
        val spinner: Spinner = binding.filterSpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.feed_filter_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterData(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

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
                filterData(binding.filterSpinner.selectedItemPosition)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FeedFragment", "Database error: ${error.message}")
            }
        })
    }

    private fun filterData(position: Int) {
        val currentUserId = auth.currentUser?.uid
        filteredItems.clear()
        when (position) {
            0 -> {
                // 전체 게시물 보기
                filteredItems.addAll(items)
            }
            1 -> {
                // 내가 작성한 게시물 모아보기
                filteredItems.addAll(items.filter { it.uid == currentUserId })
            }
            2 -> {
                //내가 좋아요 누른 게시물 모아보기
                filteredItems.addAll(items.filter { it.likedUsers.contains(currentUserId) })
            }
        }
        rvAdapter.notifyDataSetChanged()
    }

    private fun checkUserLocation() {
        val currentUserId = auth.currentUser?.uid
        currentUserId?.let {
            val locationRef = FirebaseDatabase.getInstance().getReference("location").child(it)
            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        // Location exists, navigate to MapNewsFragment
                        findNavController().navigate(R.id.action_feedFragment_to_mapNewsFragment)
                    } else {
                        // No location, navigate to MapViewActivity
                        val intent = Intent(requireContext(), MapViewActivity::class.java)
//                        intent.putExtra("from", "feed")
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FeedFragment", "Database error: ${error.message}")
                }
            })
        }
    }
}