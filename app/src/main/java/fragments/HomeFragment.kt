//package fragments
//
//import android.content.Intent
//import android.os.AsyncTask
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.databinding.DataBindingUtil
//import androidx.navigation.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import com.unit_3.sogong_test.MapViewActivity
//import com.unit_3.sogong_test.R
//import com.unit_3.sogong_test.TrendKeywordsModel
//import com.unit_3.sogong_test.TrendRVAdapter
//import com.unit_3.sogong_test.databinding.FragmentHomeBinding
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//import java.io.IOException
//import org.jsoup.nodes.Element
//
//class HomeFragment : Fragment() {
//
//    private lateinit var binding : FragmentHomeBinding
//    private val TAG = "HomeFragment"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
//
//        binding.bottomNavigationLocal.setOnClickListener {
//            checkUserLocation()
//        }
//        binding.bottomNavigationMyKeyword.setOnClickListener {
//           it.findNavController().navigate(R.id.action_homeFragment_to_myKeywordFragment)
//        }
//        binding.bottomNavigationMyPage.setOnClickListener{
//            it.findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
//        }
//
//        binding.bottomNavigationFeed.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
//        }
//
//        // 인기뉴스 리사이클러뷰 초기화
//        binding.rvHotNews.layoutManager = LinearLayoutManager(requireContext())
//
//
//
//
//        // RecyclerView 초기화
//        binding.rv.layoutManager = LinearLayoutManager(requireContext())
//
//        // Jsoup를 사용하여 인기 검색어를 가져오는 작업을 실행합니다
//        FetchTrendingKeywordsTask().execute()
//
//
//
//        return binding.root
//
//    }
//
//    private inner class FetchTrendingKeywordsTask : AsyncTask<Void, Void, MutableList<TrendKeywordsModel>>() {
//        override fun doInBackground(vararg params: Void?): MutableList<TrendKeywordsModel> {
//            val trendingKeywords = mutableListOf<TrendKeywordsModel>()
//            try {
//                val doc: Document = Jsoup.connect("https://trends.google.co.kr/trends/trendingsearches/daily/rss?geo=KR").get()
//                val items: List<Element> = doc.select("item") // RSS 피드에서 item 요소 선택
//
//                for (item in items) {
//                    val title = item.selectFirst("title")?.text()
//                    val searchCount = item.selectFirst("ht|approx_traffic")?.text()
//                    val imageUrl = item.selectFirst("ht|picture")?.text()
//
//                    trendingKeywords.add(TrendKeywordsModel(title.toString(), searchCount.toString(), imageUrl.toString()))
//
//                }
//            } catch (e: IOException) {
//                Log.e(TAG, "IOException occurred while fetching data", e)
//            } catch (e: Exception) {
//                Log.e(TAG, "Exception occurred while fetching data", e)
//            }
//            return trendingKeywords
//        }
//
//        override fun onPostExecute(result: MutableList<TrendKeywordsModel>?) {
//            if (result != null) {
//                super.onPostExecute(result.toMutableList())
//            }
//            if (result != null) {
//                if (result.isNotEmpty()) {
//                    for (keyword in result) {
//                        Log.d(TAG, "Trending Keyword: $keyword")
//                    }
//                    binding.rv.adapter = TrendRVAdapter(result)
//
//
//                } else {
//                    Log.d(TAG, "No trending keywords found")
//                    Toast.makeText(requireContext(), "인기 검색어를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
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
////                        intent.putExtra("from", "home")
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

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.unit_3.sogong_test.*
import com.unit_3.sogong_test.databinding.FragmentHomeBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val TAG = "HomeFragment"
    private val hotNewsList = mutableListOf<HotNewsModel>()
    private lateinit var hotNewsAdapter: HotNewsRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

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

        // Fetch hot news
        FetchHotNewsTask().execute()

        // Initialize trending keywords RecyclerView
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        // Fetch trending keywords
        FetchTrendingKeywordsTask().execute()

        // Setup help button click listener
        binding.buttonHelp.setOnClickListener {
            showHelpDialog()
        }

        return binding.root
    }

    private fun showHelpDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일별 인기 급상승 검색어")
        builder.setMessage("일별 인기 급상승 검색어는 지난 24시간 동안의 모든 검색어 중에서 트래픽이 크게 증가한 검색어를 강조표시하며, 1시간마다 업데이트됩니다. 이러한 인기 급상승 검색어 페이지에는 특정 검색어 및 검색 횟수의 절댓값이 표시됩니다.")
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private inner class FetchHotNewsTask : AsyncTask<Void, Void, MutableList<HotNewsModel>>() {
        override fun doInBackground(vararg params: Void?): MutableList<HotNewsModel> {
            val hotNews = mutableListOf<HotNewsModel>()
            try {
                val url = "https://media.naver.com/press/001/ranking?type=popular"
                val doc: Document = Jsoup.connect(url).get()

                // 1위부터 10위까지
                for (i in 1..10) {
                    val titleElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(3) > ul > li:nth-child($i) > a > div.list_content > strong")
                    val linkElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(3) > ul > li:nth-child($i) > a")

                    if (titleElement != null && linkElement != null) {
                        val title = titleElement.text()
                        val link = linkElement.attr("href")
                        val imageUrl = fetchImageUrlFromArticle(link)

                        hotNews.add(HotNewsModel(title, imageUrl, link))
                    }
                }

                // 11위부터 20위까지
                for (i in 1..10) {
                    val titleElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(4) > ul > li:nth-child($i) > a > div.list_content > strong")
                    val linkElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(4) > ul > li:nth-child($i) > a")

                    if (titleElement != null && linkElement != null) {
                        val title = titleElement.text()
                        val link = linkElement.attr("href")
                        val imageUrl = fetchImageUrlFromArticle(link)

                        hotNews.add(HotNewsModel(title, imageUrl, link))
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException occurred while fetching data", e)
            } catch (e: Exception) {
                Log.e(TAG, "Exception occurred while fetching data", e)
            }
            return hotNews
        }

        private fun fetchImageUrlFromArticle(articleUrl: String): String {
            return try {
                val articleDoc: Document = Jsoup.connect(articleUrl).get()
                val imageElement: Element? = articleDoc.selectFirst("meta[property=og:image]")
                imageElement?.attr("content") ?: ""
            } catch (e: IOException) {
                Log.e(TAG, "IOException occurred while fetching image from article", e)
                ""
            } catch (e: Exception) {
                Log.e(TAG, "Exception occurred while fetching image from article", e)
                ""
            }
        }

        override fun onPostExecute(result: MutableList<HotNewsModel>?) {
            super.onPostExecute(result)
            if (result != null) {
                hotNewsAdapter.updateNewsList(result)
            } else {
                Toast.makeText(requireContext(), "Failed to fetch hot news", Toast.LENGTH_SHORT).show()
            }
        }
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
            super.onPostExecute(result)
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
