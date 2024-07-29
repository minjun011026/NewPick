package com.unit_3.sogong_test


import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.unit_3.sogong_test.databinding.ActivityTrendKeywordBinding
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread

class TrendKeywordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrendKeywordBinding

    val clientId = BuildConfig.Naver_Client_ID
    val clientSecret = BuildConfig.Naver_Client_Secret
    var keyword =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        keyword = intent.getStringExtra("keyword_title") ?: return
        binding.keywordTextView.text = keyword

        thread {
            val newsArticles = fetchNewsArticles(keyword)
            runOnUiThread {
                setupRecyclerView(newsArticles)
            }
        }




        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun fetchNewsArticles(keyword: String): List<NewsModel> {
        val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
        val apiURL = "https://openapi.naver.com/v1/search/news.json?query=$encodedKeyword&display=10&start=1&sort=sim"
        val url = URL(apiURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("X-Naver-Client-Id", clientId)
        connection.setRequestProperty("X-Naver-Client-Secret", clientSecret)

        val responseCode = connection.responseCode
        if (responseCode != 200) {
            Log.e("TrendKeywordActivity", "API call failed with response code $responseCode")
            return emptyList()
        }

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()

        return parseNewsArticles(response)
    }

    private fun parseNewsArticles(response: String): List<NewsModel> {
        val articles = mutableListOf<NewsModel>()
        val root = JSONObject(response)
        val items = root.getJSONArray("items")

        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
//            val title = item.getString("title")
//            val description = item.getString("description")
            val title = item.getString("title").stripHtmlAndDecodeEntities()
            val description = item.getString("description").stripHtmlAndDecodeEntities()
            val link = item.getString("link")
            val imageUrl = fetchImageUrlFromArticle(link)

            articles.add(NewsModel(keyword, title, description, link, imageUrl))
        }

        return articles
    }

    private fun fetchImageUrlFromArticle(articleUrl: String): String? {
        return try {
            val doc = Jsoup.connect(articleUrl).get()
            val imageElement = doc.select("meta[property=og:image]").first()
            imageElement?.attr("content")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupRecyclerView(newsArticles: List<NewsModel>) {
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = NewsRVAdapter(this, newsArticles)
    }

    fun String.stripHtmlAndDecodeEntities(): String {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString().trim()
    }
}
