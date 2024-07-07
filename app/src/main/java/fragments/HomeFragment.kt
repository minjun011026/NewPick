package fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unit_3.sogong_test.CategoryEconomyActivity
import com.unit_3.sogong_test.CategoryEntertainmentActivity
import com.unit_3.sogong_test.CategoryGlobalActivity
import com.unit_3.sogong_test.CategoryLifeActivity
import com.unit_3.sogong_test.CategoryPoliticsActivity
import com.unit_3.sogong_test.CategoryScienceActivity
import com.unit_3.sogong_test.CategorySocietyActivity
import com.unit_3.sogong_test.CategorySportsActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.TrendRVAdapter
import com.unit_3.sogong_test.databinding.FragmentHomeBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import org.jsoup.nodes.Element

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val TAG = "HomeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.categoryPoliticsBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryPoliticsActivity::class.java))
        }
        binding.categoryEconomyBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryEconomyActivity::class.java))
        }
        binding.categorySocietyBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategorySocietyActivity::class.java))
        }
        binding.categoryLifeBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryLifeActivity::class.java))
        }
        binding.categorySportsBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategorySportsActivity::class.java))
        }
        binding.categoryEntertainmentBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryEntertainmentActivity::class.java))
        }
        binding.categoryScienceBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryScienceActivity::class.java))
        }
        binding.categoryGlobalBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryGlobalActivity::class.java))
        }


        binding.bottomNavigationChat.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
           it.findNavController().navigate(R.id.action_homeFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        // RecyclerView 초기화
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        // Jsoup를 사용하여 인기 검색어를 가져오는 작업을 실행합니다
        FetchTrendingKeywordsTask().execute()



        return binding.root

    }

    private inner class FetchTrendingKeywordsTask : AsyncTask<Void, Void, List<String>>() {
        override fun doInBackground(vararg params: Void?): List<String> {
            val trendingKeywords = mutableListOf<String>()
            try {
                val doc: Document = Jsoup.connect("https://trends.google.co.kr/trends/trendingsearches/daily/rss?geo=KR").get()
                val items: List<Element> = doc.select("item") // RSS 피드에서 item 요소 선택

                for (item in items) {
                    val title = item.selectFirst("title")?.text()
                    title?.let {
                        trendingKeywords.add(it)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException occurred while fetching data", e)
            } catch (e: Exception) {
                Log.e(TAG, "Exception occurred while fetching data", e)
            }
            return trendingKeywords
        }

        override fun onPostExecute(result: List<String>) {
            super.onPostExecute(result)
            if (result.isNotEmpty()) {
                for (keyword in result) {
                    Log.d(TAG, "Trending Keyword: $keyword")
                }
                binding.rv.adapter = TrendRVAdapter(result)


            } else {
                Log.d(TAG, "No trending keywords found")
                Toast.makeText(requireContext(), "인기 검색어를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}