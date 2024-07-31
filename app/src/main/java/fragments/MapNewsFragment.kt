package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.core.view.ViewCompat
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
import com.unit_3.sogong_test.ApiSearchNews
import com.unit_3.sogong_test.KeywordNewsAdapter
import com.unit_3.sogong_test.KeywordNewsModel
import com.unit_3.sogong_test.MainActivity
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentMapNewsBinding


class MapNewsFragment : Fragment() {
    private val database = Firebase.database
    val myRef = database.getReference("location")
    lateinit var recyclerview : RecyclerView
    private lateinit var binding : FragmentMapNewsBinding
    val cities = ArrayList<String>()
    private lateinit var popupWindow: PopupWindow

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize PopupWindow
        val popupView = layoutInflater.inflate(R.layout.mapnews_popup, null)
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        val v= inflater.inflate(R.layout.fragment_map_news, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_news, container, false)


        binding.bottomNavigationHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapNewsFragment_to_homeFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapNewsFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener{
            it.findNavController().navigate(R.id.action_mapNewsFragment_to_myPageFragment)
        }

        binding.bottomNavigationFeed.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapNewsFragment_to_feedFragment)
        }


//        val spinner = v.findViewById<Spinner>(R.id.city_spinner)
        val spinner = binding.citySpinner
//        recyclerview = v.findViewById(R.id.rv)
        recyclerview = binding.rv

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing cities list to avoid duplication
                cities.clear()

                // Iterate through the snapshot to get all city names
                for (snapshot in dataSnapshot.children) {
                    val city = snapshot.getValue(String::class.java)
                    if (city != null) {
                        cities.add(city)
                    }
                }
                cities.add("상세 지역 설정")
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Log.w("MapNewsFragment", "loadCity:onCancelled", databaseError.toException())
            }
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = cities[position]
                // Perform action based on the selected item
                handleCitySelection(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected (optional)
            }
        }
        if(cities.size == 1){
            startActivity(Intent(context, MapViewActivity::class.java))
        }
        return binding.root
    }



    private fun handleCitySelection(city: String) {
        when (city) {
            "상세 지역 설정" -> {
                val intent = Intent(context, MapViewActivity::class.java)
                if(cities[0] != "상세 지역 설정")
                    intent.putExtra("지역1", cities[0])
                if(cities.lastIndex > 1 && cities[1]  != "상세 지역 설정")
                    intent.putExtra("지역2", cities[1])
                startActivity(intent)
            }
            else -> {
                val newsItem = ArrayList<KeywordNewsModel>()
                val adapter = KeywordNewsAdapter(newsItem)
                recyclerview.adapter = adapter
                recyclerview.layoutManager = LinearLayoutManager(requireContext()) // 수정: requireContext() 사용

                val thread = object : Thread() {
                    override fun run() {
                        val api = ApiSearchNews
                        if (city.isNotEmpty()) {
                            val fetchedNewsItems = api.main(city)

                            // UI 스레드에서 RecyclerView를 업데이트
                            requireActivity().runOnUiThread {
                                newsItem.clear()
                                newsItem.addAll(fetchedNewsItems)
                                fetchedNewsItems.clear()
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                thread.start()
            }
        }
    }

}