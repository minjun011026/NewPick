//package fragments
//
//import HomeViewModel
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.PopupWindow
//import android.widget.Toast
//import androidx.core.view.ViewCompat
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
//import androidx.navigation.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.airbnb.lottie.LottieAnimationView
//import com.google.firebase.Firebase
//import com.google.firebase.auth.auth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.database
//import com.google.firebase.database.ktx.database
//import com.unit_3.sogong_test.*
//import com.unit_3.sogong_test.databinding.FragmentHomeBinding
//
//class HomeFragment : Fragment() {
//
//    private lateinit var binding: FragmentHomeBinding
//    private val TAG = "HomeFragment"
//    private val hotNewsList = mutableListOf<HotNewsModel>()
//    private lateinit var hotNewsAdapter: HotNewsRVAdapter
//    private lateinit var popupWindow: PopupWindow
//    private lateinit var loadingAnimationView: LottieAnimationView
//    private lateinit var loadingOverlay: View
//
//    private val viewModel: HomeViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize PopupWindow
//        val popupView = layoutInflater.inflate(R.layout.home_popup, null)
//        popupWindow = PopupWindow(
//            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true
//        )
//
//        // Set PopupWindow background and animations
//        popupWindow.setBackgroundDrawable(null)
//        popupWindow.isOutsideTouchable = true
//        popupWindow.isFocusable = true
//
//        // Apply elevation to PopupWindow
//        ViewCompat.setElevation(popupView, 8f) // Adjust elevation as needed
//
//        // Show PopupWindow when the help button is touched
//        binding.helpImageView.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // Show PopupWindow
//                    popupWindow.showAsDropDown(v, 0, 0)
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Dismiss the PopupWindow when touched outside
//        binding.root.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                if (popupWindow.isShowing) {
//                    popupWindow.dismiss()
//                }
//            }
//            false
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? {
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
//
//        // Initialize Lottie Animation View
//        loadingAnimationView = binding.loadingAnimationView
//        loadingOverlay = binding.loadingOverlay
//
//        // Navigation button setup
//        binding.bottomNavigationLocal.setOnClickListener {
//            checkUserLocation()
//        }
//        binding.bottomNavigationMyKeyword.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_myKeywordFragment)
//        }
//        binding.bottomNavigationMyPage.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
//        }
//        binding.bottomNavigationFeed.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
//        }
//
//        // Initialize hot news RecyclerView
//        binding.rvHotNews.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        hotNewsAdapter = HotNewsRVAdapter(hotNewsList)
//        binding.rvHotNews.adapter = hotNewsAdapter
//
//        // Initialize trending keywords RecyclerView
//        binding.rv.layoutManager = LinearLayoutManager(requireContext())
//
//        // Observe data from ViewModel
//        viewModel.hotNewsList.observe(viewLifecycleOwner, Observer { hotNews ->
//            hotNews?.let {
//                hotNewsAdapter.updateNewsList(it)
//            }
//            checkDataLoadCompletion()
//        })
//
//        viewModel.trendingKeywordsList.observe(viewLifecycleOwner, Observer { trendingKeywords ->
//            trendingKeywords?.let {
//                binding.rv.adapter = TrendRVAdapter(it)
//            }
//            checkDataLoadCompletion()
//        })
//
//        // Fetch data
//        showLoadingAnimation()
//        viewModel.fetchHotNews()
//        viewModel.fetchTrendingKeywords()
//
//        return binding.root
//    }
//
//    private fun showLoadingAnimation() {
//        loadingOverlay.visibility = View.VISIBLE
//        loadingAnimationView.visibility = View.VISIBLE
//        loadingAnimationView.playAnimation()
//    }
//
//    private fun hideLoadingAnimation() {
//        loadingOverlay.visibility = View.GONE
//        loadingAnimationView.visibility = View.GONE
//        loadingAnimationView.cancelAnimation()
//    }
//
//    private fun checkDataLoadCompletion() {
//        if (viewModel.hotNewsList.value != null && viewModel.trendingKeywordsList.value != null) {
//            hideLoadingAnimation()
//        }
//    }
//
//    private fun checkUserLocation() {
//        val currentUserId = Firebase.auth.currentUser?.uid
//        currentUserId?.let {
//            val locationRef = Firebase.database.getReference("location").child(it)
//            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists() && snapshot.childrenCount > 0) {
//                        // Location exists, navigate to MapNewsFragment
//                        view?.findNavController()?.navigate(R.id.action_homeFragment_to_mapNewsFragment)
//                    } else {
//                        // No location, navigate to MapViewActivity
//                        val intent = Intent(requireContext(), MapViewActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("MyKeywordFragment", "Database error: ${error.message}")
//                }
//            })
//        }
//    }
//}


package fragments

import HomeViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.unit_3.sogong_test.*
import com.unit_3.sogong_test.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val TAG = "HomeFragment"
    private val hotNewsList = mutableListOf<HotNewsModel>()
    private lateinit var hotNewsAdapter: HotNewsRVAdapter
    private lateinit var popupWindow: PopupWindow
    private lateinit var loadingAnimationView: LottieAnimationView
    private lateinit var loadingOverlay: View
    private lateinit var loadingTextView : TextView

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize PopupWindow
        val popupView = layoutInflater.inflate(R.layout.home_popup, null)
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set PopupWindow background and animations
        popupWindow.setBackgroundDrawable(null)
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Apply elevation to PopupWindow
        ViewCompat.setElevation(popupView, 8f) // Adjust elevation as needed

        // Show PopupWindow when the help button is touched
        binding.helpImageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Show PopupWindow
                    popupWindow.showAsDropDown(v, 0, 0)
                    true
                }
                else -> false
            }
        }

        // Dismiss the PopupWindow when touched outside
        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
            }
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // Initialize Lottie Animation View
        loadingAnimationView = binding.loadingAnimationView
        loadingOverlay = binding.loadingOverlay

        // Navigation button setup
        binding.bottomNavigationLocal.setOnClickListener {
            checkUserLocation()
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }
        binding.bottomNavigationFeed.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }

        // Initialize hot news RecyclerView
        binding.rvHotNews.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hotNewsAdapter = HotNewsRVAdapter(hotNewsList)
        binding.rvHotNews.adapter = hotNewsAdapter

        // Initialize trending keywords RecyclerView
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        // Observe data from ViewModel
        viewModel.hotNewsList.observe(viewLifecycleOwner, Observer { hotNews ->
            hotNews?.let {
                hotNewsAdapter.updateNewsList(it)
            }
        })

        viewModel.trendingKeywordsList.observe(viewLifecycleOwner, Observer { trendingKeywords ->
            trendingKeywords?.let {
                binding.rv.adapter = TrendRVAdapter(it)
            }
        })

        // Observe the combined LiveData for data loading completion
        viewModel.allDataLoaded.observe(viewLifecycleOwner, Observer { allDataLoaded ->
            if (allDataLoaded) {
                hideLoadingAnimation()
            }
        })

        // Fetch data
        showLoadingAnimation()
        viewModel.fetchHotNews()
        viewModel.fetchTrendingKeywords()

        return binding.root
    }

    private fun showLoadingAnimation() {
        loadingOverlay.visibility = View.VISIBLE
        loadingAnimationView.visibility = View.VISIBLE
        loadingAnimationView.playAnimation()
    }

    private fun hideLoadingAnimation() {
        loadingOverlay.visibility = View.GONE
        loadingAnimationView.visibility = View.GONE
        loadingAnimationView.cancelAnimation()
    }

    private fun checkUserLocation() {
        val currentUserId = Firebase.auth.currentUser?.uid
        currentUserId?.let {
            val locationRef = Firebase.database.getReference("location").child(it)
            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        // Location exists, navigate to MapNewsFragment
                        view?.findNavController()?.navigate(R.id.action_homeFragment_to_mapNewsFragment)
                    } else {
                        // No location, navigate to MapViewActivity
                        val intent = Intent(requireContext(), MapViewActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyKeywordFragment", "Database error: ${error.message}")
                }
            })
        }
    }
}



