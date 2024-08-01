package com.unit_3.sogong_test

import android.text.Html
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

object ApiSearchNews {
    var clientId: String = BuildConfig.Naver_Client_ID
    var clientSecret: String = BuildConfig.Naver_Client_Secret

    lateinit var newsItem : ArrayList<KeywordNewsModel>
    fun main(keyword : String) : ArrayList<KeywordNewsModel>{
        newsItem = ArrayList()
        var text: String? = null
        try {
            text = URLEncoder.encode(keyword, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("검색어 인코딩 실패", e)
        }

        val apiURL =
            "https://openapi.naver.com/v1/search/news.json?query=$text&display=20&start=1&sort=sim"


        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
        val requestHeaders: MutableMap<String, String> = HashMap()
        requestHeaders["X-Naver-Client-Id"] = clientId
        requestHeaders["X-Naver-Client-Secret"] = clientSecret
        val responseBody = get(apiURL, requestHeaders)

        parseData(responseBody, keyword)

        return newsItem
    }

    private fun get(apiUrl: String, requestHeaders: Map<String, String>): String {
        val con = connect(apiUrl)
        try {
            con.requestMethod = "GET"
            for ((key, value) in requestHeaders)  {
                con.setRequestProperty(key, value)
            }

            val responseCode = con.responseCode
            return if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                readBody(con.inputStream)
            } else { // 에러 발생
                readBody(con.errorStream)
            }
        } catch (e: IOException) {
            throw RuntimeException("API 요청과 응답 실패", e)
        } finally {
            con.disconnect()
        }
    }

    private fun connect(apiUrl: String): HttpURLConnection {
        try {
            val url = URL(apiUrl)
            return url.openConnection() as HttpURLConnection
        } catch (e: MalformedURLException) {
            throw RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
        } catch (e: IOException) {
            throw RuntimeException("연결이 실패했습니다. : $apiUrl", e)
        }
    }

    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)

        try {
            BufferedReader(streamReader).use { lineReader ->
                val responseBody = StringBuilder()
                var line: String?
                while ((lineReader.readLine().also { line = it }) != null) {
                    responseBody.append(line)
                }
                return responseBody.toString()
            }
        } catch (e: IOException) {
            throw RuntimeException("API 응답을 읽는데 실패했습니다.", e)
        }
    }

    private fun parseData(responseBody: String, keyword: String) {
        var title: String
        var link:String
        var jsonObject: JSONObject? = null
        var imageUrl : String
        try {
            jsonObject = JSONObject(responseBody.toString())
            val jsonArray = jsonObject.getJSONArray("items")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                title = item.getString("title").stripHtmlAndDecodeEntities()
                link = item.getString("link")
                imageUrl = fetchImageUrlFromArticle(link).toString()

                newsItem.add(KeywordNewsModel(keyword, title, link, imageUrl))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("ParseDataError", "PAAAARRRSE")
        }
    }

    fun String.stripHtmlAndDecodeEntities(): String {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString().trim()
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

}