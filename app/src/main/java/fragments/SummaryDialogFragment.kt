package fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.unit_3.sogong_test.BuildConfig
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.WebViewActivity
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class SummaryDialogFragment : DialogFragment(), TextToSpeech.OnInitListener {

    private val client = OkHttpClient()
    private var url: String? = null
    private lateinit var tts: TextToSpeech
    private var summaryText: String = ""

    val clientId = BuildConfig.Naver_Clova_Client_ID
    val clientSecret = BuildConfig.Naver_Clova_Client_Secret

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(ARG_URL)
        }
        tts = TextToSpeech(context, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_summary, container, false)

        val summaryTextView = view.findViewById<TextView>(R.id.summaryTextView)
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        val toWebViewBtn = view.findViewById<Button>(R.id.toWebViewBtn)
        val speakerBtn = view.findViewById<ImageButton>(R.id.speakerBtn)

        url?.let {
            getArticleContent(it) { content ->
                Log.d("SummaryFragment", "Article content: $content")
                getSummary(content) { summary ->
                    activity?.runOnUiThread {
                        summaryText = summary
                        summaryTextView.text = summary
                    }
                }
            }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
        toWebViewBtn.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("link", url)
            startActivity(intent)
        }
        speakerBtn.setOnClickListener {
            tts.speak(summaryText, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        return view
    }

    private fun getArticleContent(url: String, callback: (String) -> Unit) {
        Thread {
            try {
                Log.d("SummaryFragment", "Fetching article content from URL: $url")
                val document = Jsoup.connect(url).get()

                // 기사 제목 추출
                val title = document.title()
                Log.d("SummaryFragment", "Title: $title")

                // 기사 본문을 추출하는 로직
                var content = document.select("article#dic_area").text()
                Log.d("SummaryFragment", "article#dic_area : $content")
                if (content.isBlank()) {
                    content = document.select("div#_article_content").text()
                    Log.d("SummaryFragment", "div#_article_content : $content")
                }
                if (content.isBlank()) {
                    content = document.select("#articeBody").text()
                    Log.d("SummaryFragment", "div.news_end : $content")
                }
                if (content.isBlank()) {
                    content = document.select("#comp_news_article > div._article_content").text()
                    Log.d("SummaryFragment", "#comp_news_article > div._article_content : $content")
                }
                if (content.isBlank()) {
                    content = document.select("div#content").text()
                    Log.d("SummaryFragment", "div#content : $content")
                }

                Log.d("SummaryFragment", "Fetched content: $content")

                callback(content)
            } catch (e: IOException) {
                Log.e("SummaryFragment", "Failed to retrieve article content", e)
                callback("Failed to retrieve article content")
            }
        }.start()
    }

    private fun getSummary(content: String, callback: (String) -> Unit) {
        val apiUrl = "https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize"

        val json = JSONObject().apply {
            put("document", JSONObject().apply {
                put("content", content)
            })
            put("option", JSONObject().apply {
                put("language", "ko")
                put("model", "news")
                put("tone", 0)
                put("summaryCount", 3)
            })
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(apiUrl)
            .post(body)
            .addHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .addHeader("X-NCP-APIGW-API-KEY", clientSecret)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SummaryFragment", "Failed to summarize", e)
                callback("Failed to summarize")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)
                    val summary = jsonResponse.getString("summary")
                    Log.d("SummaryFragment", "Summary: $summary")
                    callback(summary)
                } else {
                    Log.e("SummaryFragment", "Error: response body is null")
                    callback("일부 기사는 요약을 제공하지 않을 수 있습니다.")
                }
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.KOREAN
        } else {
            Log.e("SummaryFragment", "TTS 초기화 실패")
        }
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    companion object {
        private const val ARG_URL = "url"

        @JvmStatic
        fun newInstance(url: String) =
            SummaryDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                }
            }
    }
}
