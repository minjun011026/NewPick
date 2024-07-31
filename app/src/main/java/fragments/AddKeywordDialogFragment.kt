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
import android.widget.Toast
import android.widget.Button
import android.graphics.Color
import android.util.Log


class AddKeywordDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddKeywordDialogBinding
    private var recommendedKeywords: List<String> = emptyList() // 추천 키워드 리스트
    private val selectedKeywords = mutableListOf<String>() // 선택된 키워드 리스트
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
    // 추천 키워드가 선택되었을 때 호출되는 메서드
    fun setRecommendedKeywords(recommendations: List<String>) {
        recommendedKeywords = recommendations
        onRecommendedKeywordsFetched(recommendations) // 추천 키워드 버튼 생성
    }

    private fun fetchRecommendedKeywords(keyword: String) {
        val client = OkHttpClient()
        val requestBodyJson = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", "이 사용자가 추가할 법한 관련 키워드를 JSON 배열 형식으로, 각 키워드를 큰따옴표로 감싸고 쉼표로 구분하여 3개 추천해줘: $keyword\n")))
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

                    // 응답 본문을 문자열로 가져오기
                    val responseBody = it.body!!.string()
                    Log.d("API Response", responseBody)

                    val jsonResponse = JSONObject(responseBody)
                    // JSON 응답에서 추천 키워드 가져오기
                    val recommendationsString = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    // JSON 배열로 변환
                    val recommendations = JSONArray(recommendationsString).let { jsonArray ->
                        List(jsonArray.length()) { jsonArray.getString(it).trim() }
                    }

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
//        if (!isAdded) return // 프래그먼트가 활성 상태가 아닐 경우 종료

        val dialog = RecommendedKeywordsDialogFragment.newInstance(recommendations) { selectedKeywords ->
            saveRecommendedKeywords(selectedKeywords) // 선택된 키워드 저장
        }
        dialog.show(childFragmentManager, "RecommendedKeywordsDialog")
    }



    private fun saveRecommendedKeywords(selectedKeywords: List<String>) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
        for (keyword in selectedKeywords) {
            myRef.push().setValue(KeywordModel(keyword))
        }
        dismiss()
    }
    // 키워드 추천 후 추천 키워드를 다이얼로그로 표시하는 메서드 추가
    private fun onRecommendedKeywordsFetched(recommendations: List<String>) {
        recommendedKeywords = recommendations

        // 추천 키워드를 버튼 형식으로 표시
        val buttonLayout = binding.buttonLayout // 버튼을 추가할 레이아웃

        // 기존 버튼 제거
        buttonLayout.removeAllViews()

        recommendations.forEach { keyword ->
            val button = Button(context).apply {
                text = keyword
                setOnClickListener {
                    if (selectedKeywords.contains(keyword)) {
                        selectedKeywords.remove(keyword) // 이미 선택된 키워드라면 제거
                        setBackgroundColor(Color.LTGRAY) // 선택 해제 시 색상 변경
                    } else {
                        selectedKeywords.add(keyword) // 선택된 키워드 추가
                        setBackgroundColor(Color.GREEN) // 선택 시 색상 변경
                    }
                }
            }
            buttonLayout.addView(button)
        }

        // 완료 버튼 추가
        val completeButton = Button(context).apply {
            text = "완료"
            setOnClickListener {
                if (selectedKeywords.isNotEmpty()) {
                    saveRecommendedKeywords(selectedKeywords)
                    dismiss() // 다이얼로그 종료
                } else {
                    Toast.makeText(context, "키워드를 선택하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        buttonLayout.addView(completeButton)
    }

}
