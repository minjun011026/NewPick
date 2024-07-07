package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unit_3.sogong_test.databinding.ActivityTrendKeywordBinding
import org.json.JSONObject
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

    lateinit var searchWord: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 인텐트에서 전달받은 키워드 가져오기
        val keyword = intent.getStringExtra("keyword_title")

        // 가져온 키워드를 TextView에 설정
        binding.keywordTextView.text = "$keyword"

        //네이버 뉴스 검색 API로 keyword에 해당하는 뉴스 가져와서 recyclerview에 올리기

        binding.run {
            thread {
                try {
                    searchWord = URLEncoder.encode(keyword, "UTF-8")
                    Log.d("검색어 인코딩 성공", searchWord)
                } catch (e: Exception) {
                    Log.d("검색어 인코딩 실패", e.toString())
                }

                // 파라미터
                // query : 검색어(필수), UTF-8로 인코딩
                // display : 한 번에 표시할 검색 결과 개수 (기본값: 10, 최댓값: 100)
                // start : 검색 시작 위치(기본값: 1, 최댓값: 1000)
                // sort : sim(정확도순)[기본값]/date(날짜순) 내림차순 정렬
                val apiURL =
                    "https://openapi.naver.com/v1/search/news.json?query=$searchWord&display=10&start=1&sort=sim"
                // URL 객체 생성
                val url = URL(apiURL)
                // 접속 후 스트림 추출
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                // httpUrlConnection 객체 메서드로 헤더 설정
                httpURLConnection.setRequestProperty("X-Naver-Client-Id", clientId)
                httpURLConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret)

                val responseCode = httpURLConnection.responseCode

//                 Log.d("response", responseCode.toString())


                val reader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                val stringBuffer = StringBuffer()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuffer.append(line)
                }
                reader.close()

                val data = stringBuffer.toString()

//                 Log.d("data", data)


                // JSON 파일 읽기 코드에 이어서 작성

                // 먼저 최상위가 `{}` 이므로 JSONObject를 사용한다
                val root = JSONObject(data)

                // items은 []안에 {}로 구분되어 있다
                // 먼저 getJSONArray를 이용해 items를 추출한다
                val itemArray = root.getJSONArray("items")

                for (idx in 0 until itemArray.length()) {

                    // items의 기사들은 {}로 감싸져있기에 getJSONObject 이용해 idx 번째 JSONObject를 추출한다
                    val itemObject = itemArray.getJSONObject(idx)
                    // 원하는 데이터들을 추출한다
                    val title = itemObject.getString("title")
                    val description = itemObject.getString("description")
                    val link = itemObject.getString("link")

                    Log.d("data", title)
                    Log.d("data", description)
                    Log.d("data", link)
                    Log.d("data", "-------------------------------------------------------")
                }
            }

        }




        binding.backBtn.setOnClickListener {
            navigateBackToMainActivity()
        }
    }
        private fun navigateBackToMainActivity() {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }
}