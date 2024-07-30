package fragments

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.unit_3.sogong_test.KeywordModel
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentAddKeywordDialogBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import kotlin.concurrent.thread
import com.unit_3.sogong_test.BuildConfig


class AddKeywordDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddKeywordDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_keyword_dialog, container, false)

        binding.registerBtn.setOnClickListener {
            val keyword = binding.keywordArea.text.toString()

            if (keyword.isNotEmpty()) {
                // 네트워크 작업은 별도의 스레드에서 실행
                thread {
                    fetchImageUrlFromGoogle(keyword, { imageUrl ->
                        activity?.runOnUiThread {
                            saveKeywordToFirebase(keyword, imageUrl)
                        }
                    }, { error ->
                        // Handle the error
                        activity?.runOnUiThread {
                            dismiss()
                        }
                    })
                }
            }
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun fetchImageUrlFromGoogle(query: String, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val encodedQuery = query.replace(" ", "+")
        val url = "https://www.google.com/search?hl=en&tbm=isch&q=$encodedQuery"

        try {
            val document: Document = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
            val imageElements: Elements = document.select("img")

            // googlelogo_desk_heirloom_color_150x55dp.gif을 필터링 하기 위해 src 속성 및 data-src 속성을 사용하여 이미지 URL을 가져옵니다.
            val firstImageUrl = imageElements
                .asSequence()
                .mapNotNull { it.attr("data-src").ifEmpty { it.attr("src") } }
                .filter { it.isNotEmpty() && !it.contains("googlelogo") }
                .firstOrNull()

            if (firstImageUrl != null) {
                onSuccess(firstImageUrl)
            } else {
                onFailure(Throwable("No image found"))
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun saveKeywordToFirebase(keyword: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
        myRef.push().setValue(KeywordModel(keyword, "", false, imageUrl)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 키워드 저장 후 추천 키워드 가져오기
                fetchRecommendedKeywords(keyword)
            } else {
                // 실패 처리 (예: 사용자에게 메시지 표시)
            }
        }
    }

    private fun fetchRecommendedKeywords(keyword: String) {
        val client = OkHttpClient()
        val requestBodyJson = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", "Suggest 3 related keywords for: $keyword")))
            put("max_tokens", 50)
        }.toString()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestBodyJson.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // 실패 처리 (예: 사용자에게 메시지 표시)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) throw IOException("Unexpected code $it")

                    val jsonResponse = JSONObject(it.body!!.string())
                    val recommendations = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .split(",")

                    // 프래그먼트가 유효한지 확인
                    if (isAdded) {
                        // 추천 키워드 다이얼로그 표시
                        showRecommendedKeywordsDialog(recommendations)
                    }
                }
            }
        })
    }


    private fun showRecommendedKeywordsDialog(recommendations: List<String>) {
        if (!isAdded) return // 프래그먼트가 활성 상태가 아닐 경우 종료

        val dialog = RecommendedKeywordsDialogFragment.newInstance(recommendations) { selectedKeywords ->
            saveRecommendedKeywords(selectedKeywords)
        }
        dialog.show(childFragmentManager, "RecommendedKeywordsDialog")
    }

    private fun saveRecommendedKeywords(selectedKeywords: List<String>) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
        for (keyword in selectedKeywords) {
            myRef.push().setValue(KeywordModel(keyword))
        }
    }
}

