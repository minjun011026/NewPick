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
import androidx.fragment.app.Fragment
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
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R


class MapNewsFragment : Fragment() {
    private val database = Firebase.database
    val myRef = database.getReference("lcation")
    lateinit var recyclerview : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_map_news, container, false)
        val spinner = v.findViewById<Spinner>(R.id.city_spinner)
        val cities = ArrayList<String>()
        recyclerview = v.findViewById(R.id.rv)

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

        return v
    }

    private fun handleCitySelection(city: String) {
        when (city) {
            "상세 지역 설정" -> {
                startActivity(Intent(context, MapViewActivity::class.java))
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