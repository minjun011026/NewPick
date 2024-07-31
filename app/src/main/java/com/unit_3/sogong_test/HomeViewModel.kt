//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.unit_3.sogong_test.HotNewsModel
//import com.unit_3.sogong_test.TrendKeywordsModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//import org.jsoup.nodes.Element
//import java.io.IOException
//
//class HomeViewModel : ViewModel() {
//
//    private val _hotNewsList = MutableLiveData<MutableList<HotNewsModel>>()
//    val hotNewsList: LiveData<MutableList<HotNewsModel>> get() = _hotNewsList
//
//    private val _trendingKeywordsList = MutableLiveData<MutableList<TrendKeywordsModel>>()
//    val trendingKeywordsList: LiveData<MutableList<TrendKeywordsModel>> get() = _trendingKeywordsList
//
//    fun fetchHotNews() {
//        viewModelScope.launch {
//            val hotNews = withContext(Dispatchers.IO) {
//                val newsList = mutableListOf<HotNewsModel>()
//                try {
//                    val url = "https://media.naver.com/press/001/ranking?type=popular"
//                    val doc: Document = Jsoup.connect(url).get()
//
//                    for (i in 1..10) {
//                        val titleElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(3) > ul > li:nth-child($i) > a > div.list_content > strong")
//                        val linkElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(3) > ul > li:nth-child($i) > a")
//
//                        if (titleElement != null && linkElement != null) {
//                            val title = titleElement.text()
//                            val link = linkElement.attr("href")
//                            val imageUrl = fetchImageUrlFromArticle(link)
//
//                            newsList.add(HotNewsModel(title, imageUrl, link))
//                        }
//                    }
//
//                    for (i in 1..10) {
//                        val titleElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(4) > ul > li:nth-child($i) > a > div.list_content > strong")
//                        val linkElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(4) > ul > li:nth-child($i) > a")
//
//                        if (titleElement != null && linkElement != null) {
//                            val title = titleElement.text()
//                            val link = linkElement.attr("href")
//                            val imageUrl = fetchImageUrlFromArticle(link)
//
//                            newsList.add(HotNewsModel(title, imageUrl, link))
//                        }
//                    }
//                } catch (e: IOException) {
//                    Log.e("HomeViewModel", "IOException occurred while fetching data", e)
//                } catch (e: Exception) {
//                    Log.e("HomeViewModel", "Exception occurred while fetching data", e)
//                }
//                newsList
//            }
//            _hotNewsList.postValue(hotNews)
//        }
//    }
//
//    fun fetchTrendingKeywords() {
//        viewModelScope.launch {
//            val trendingKeywords = withContext(Dispatchers.IO) {
//                val keywordsList = mutableListOf<TrendKeywordsModel>()
//                try {
//                    val doc: Document = Jsoup.connect("https://trends.google.co.kr/trends/trendingsearches/daily/rss?geo=KR").get()
//                    val items: List<Element> = doc.select("item")
//
//                    for (item in items) {
//                        val title = item.selectFirst("title")?.text()
//                        val searchCount = item.selectFirst("ht|approx_traffic")?.text()
//                        val imageUrl = item.selectFirst("ht|picture")?.text()
//
//                        keywordsList.add(TrendKeywordsModel(title.toString(), searchCount.toString(), imageUrl.toString()))
//                    }
//                } catch (e: IOException) {
//                    Log.e("HomeViewModel", "IOException occurred while fetching data", e)
//                } catch (e: Exception) {
//                    Log.e("HomeViewModel", "Exception occurred while fetching data", e)
//                }
//                keywordsList
//            }
//            _trendingKeywordsList.postValue(trendingKeywords)
//        }
//    }
//
//    private fun fetchImageUrlFromArticle(articleUrl: String): String {
//        return try {
//            val articleDoc: Document = Jsoup.connect(articleUrl).get()
//            val imageElement: Element? = articleDoc.selectFirst("meta[property=og:image]")
//            imageElement?.attr("content") ?: ""
//        } catch (e: IOException) {
//            Log.e("HomeViewModel", "IOException occurred while fetching image from article", e)
//            ""
//        } catch (e: Exception) {
//            Log.e("HomeViewModel", "Exception occurred while fetching image from article", e)
//            ""
//        }
//    }
//}


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unit_3.sogong_test.HotNewsModel
import com.unit_3.sogong_test.TrendKeywordsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException

class HomeViewModel : ViewModel() {

    private val _hotNewsList = MutableLiveData<MutableList<HotNewsModel>>()
    val hotNewsList: LiveData<MutableList<HotNewsModel>> get() = _hotNewsList

    private val _trendingKeywordsList = MutableLiveData<MutableList<TrendKeywordsModel>>()
    val trendingKeywordsList: LiveData<MutableList<TrendKeywordsModel>> get() = _trendingKeywordsList

    private val _allDataLoaded = MediatorLiveData<Boolean>().apply {
        addSource(hotNewsList) { checkDataLoadCompletion() }
        addSource(trendingKeywordsList) { checkDataLoadCompletion() }
    }
    val allDataLoaded: LiveData<Boolean> get() = _allDataLoaded

    private var hotNewsLoaded = false
    private var trendingKeywordsLoaded = false

    fun fetchHotNews() {
        viewModelScope.launch {
            val hotNews = withContext(Dispatchers.IO) {
                val newsList = mutableListOf<HotNewsModel>()
                try {
                    val url = "https://media.naver.com/press/001/ranking?type=popular"
                    val doc: Document = Jsoup.connect(url).get()

                    for (i in 1..10) {
                        val titleElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(3) > ul > li:nth-child($i) > a > div.list_content > strong")
                        val linkElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(3) > ul > li:nth-child($i) > a")

                        if (titleElement != null && linkElement != null) {
                            val title = titleElement.text()
                            val link = linkElement.attr("href")
                            val imageUrl = fetchImageUrlFromArticle(link)

                            newsList.add(HotNewsModel(title, imageUrl, link))
                        }
                    }

                    for (i in 1..10) {
                        val titleElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(4) > ul > li:nth-child($i) > a > div.list_content > strong")
                        val linkElement: Element? = doc.selectFirst("#ct > div.press_ranking_home > div:nth-child(4) > ul > li:nth-child($i) > a")

                        if (titleElement != null && linkElement != null) {
                            val title = titleElement.text()
                            val link = linkElement.attr("href")
                            val imageUrl = fetchImageUrlFromArticle(link)

                            newsList.add(HotNewsModel(title, imageUrl, link))
                        }
                    }
                } catch (e: IOException) {
                    Log.e("HomeViewModel", "IOException occurred while fetching data", e)
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Exception occurred while fetching data", e)
                }
                newsList
            }
            _hotNewsList.postValue(hotNews)
            hotNewsLoaded = true
            checkDataLoadCompletion()
        }
    }

    fun fetchTrendingKeywords() {
        viewModelScope.launch {
            val trendingKeywords = withContext(Dispatchers.IO) {
                val keywordsList = mutableListOf<TrendKeywordsModel>()
                try {
                    val doc: Document = Jsoup.connect("https://trends.google.co.kr/trends/trendingsearches/daily/rss?geo=KR").get()
                    val items: List<Element> = doc.select("item")

                    for (item in items) {
                        val title = item.selectFirst("title")?.text()
                        val searchCount = item.selectFirst("ht|approx_traffic")?.text()
                        val imageUrl = item.selectFirst("ht|picture")?.text()

                        keywordsList.add(TrendKeywordsModel(title.toString(), searchCount.toString(), imageUrl.toString()))
                    }
                } catch (e: IOException) {
                    Log.e("HomeViewModel", "IOException occurred while fetching data", e)
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Exception occurred while fetching data", e)
                }
                keywordsList
            }
            _trendingKeywordsList.postValue(trendingKeywords)
            trendingKeywordsLoaded = true
            checkDataLoadCompletion()
        }
    }

    private fun checkDataLoadCompletion() {
        _allDataLoaded.value = hotNewsLoaded && trendingKeywordsLoaded
    }

    private fun fetchImageUrlFromArticle(articleUrl: String): String {
        return try {
            val articleDoc: Document = Jsoup.connect(articleUrl).get()
            val imageElement: Element? = articleDoc.selectFirst("meta[property=og:image]")
            imageElement?.attr("content") ?: ""
        } catch (e: IOException) {
            Log.e("HomeViewModel", "IOException occurred while fetching image from article", e)
            ""
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Exception occurred while fetching image from article", e)
            ""
        }
    }
}



