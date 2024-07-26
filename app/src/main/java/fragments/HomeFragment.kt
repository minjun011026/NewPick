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
import com.unit_3.sogong_test.MapViewActivity
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.TrendKeywordsModel
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

        binding.bottomNavigationLocal.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_mapNewsFragment)
        }
        binding.bottomNavigationMyKeyword.setOnClickListener {
           it.findNavController().navigate(R.id.action_homeFragment_to_myKeywordFragment)
        }
        binding.bottomNavigationMyPage.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        binding.bottomNavigationFeed.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }

        // RecyclerView 초기화
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        // Jsoup를 사용하여 인기 검색어를 가져오는 작업을 실행합니다
        FetchTrendingKeywordsTask().execute()



        return binding.root

    }

    private inner class FetchTrendingKeywordsTask : AsyncTask<Void, Void, MutableList<TrendKeywordsModel>>() {
        override fun doInBackground(vararg params: Void?): MutableList<TrendKeywordsModel> {
            val trendingKeywords = mutableListOf<TrendKeywordsModel>()
            try {
                val doc: Document = Jsoup.connect("https://trends.google.co.kr/trends/trendingsearches/daily/rss?geo=KR").get()
                val items: List<Element> = doc.select("item") // RSS 피드에서 item 요소 선택

                for (item in items) {
                    val title = item.selectFirst("title")?.text()
                    val searchCount = item.selectFirst("ht|approx_traffic")?.text()
                    val imageUrl = item.selectFirst("ht|picture")?.text()

                    trendingKeywords.add(TrendKeywordsModel(title.toString(), searchCount.toString(), imageUrl.toString()))

                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException occurred while fetching data", e)
            } catch (e: Exception) {
                Log.e(TAG, "Exception occurred while fetching data", e)
            }
            return trendingKeywords
        }

        override fun onPostExecute(result: MutableList<TrendKeywordsModel>?) {
            if (result != null) {
                super.onPostExecute(result.toMutableList())
            }
            if (result != null) {
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
}