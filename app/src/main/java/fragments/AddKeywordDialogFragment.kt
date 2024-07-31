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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
    private var recommendedKeywords: List<KeywordModel> = emptyList() // 추천 키워드 리스트
    private val selectedKeywords = mutableListOf<KeywordModel>() // 선택된 키워드 리스트

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_keyword_dialog, container, false)

        binding.registerBtn.setOnClickListener {
            val keyword = binding.keywordArea.text.toString()

            if (keyword.isNotEmpty()) {
                // API 호출은 비동기적으로 수행
                thread {
                    fetchImageUrlFromGoogle(keyword, { imageUrl ->
                        activity?.runOnUiThread {
                            saveKeywordToFirebase(keyword, imageUrl)
                        }
                    }, { error ->
                        // Handle the error
                        Log.e("AddKeywordDialogFragment", "Error fetching image URL", error)
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
                Log.e("AddKeywordDialogFragment", "Failed to save keyword to Firebase")
            }
        }
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
                Log.e("AddKeywordDialogFragment", "Failed to fetch recommended keywords", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("AddKeywordDialogFragment", "Unexpected code $it")
                        return
                    }

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

                    // 추천 키워드의 이미지 URL을 가져오기 위한 리스트 초기화
                    val keywordModels = mutableListOf<KeywordModel>()
                    var remainingKeywords = recommendations.size

                    recommendations.forEach { keyword ->
                        fetchImageUrlFromGoogle(keyword, { imageUrl ->
                            keywordModels.add(KeywordModel(keyword, "", false, imageUrl))
                            remainingKeywords--

                            if (remainingKeywords == 0 && isAdded) {
                                // 모든 키워드의 이미지 URL을 가져온 후에 다이얼로그 표시
                                activity?.runOnUiThread {
                                    dismissAndShowRecommendedDialog(keywordModels)
                                }
                            }
                        }, { error ->
                            // 이미지 URL을 가져오지 못한 경우에도 리스트에 추가
                            keywordModels.add(KeywordModel(keyword, "", false, ""))
                            remainingKeywords--

                            if (remainingKeywords == 0 && isAdded) {
                                // 모든 키워드의 이미지 URL을 가져온 후에 다이얼로그 표시
                                activity?.runOnUiThread {
                                    dismissAndShowRecommendedDialog(keywordModels)
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    private fun dismissAndShowRecommendedDialog(recommendations: List<KeywordModel>) {
        dismiss()
        val dialog = RecommendedKeywordsDialogFragment.newInstance(recommendations) { selectedKeywords ->
            saveRecommendedKeywords(selectedKeywords) // 선택된 키워드 저장
        }
        dialog.show(parentFragmentManager, "RecommendedKeywordsDialog")
    }

    private fun saveRecommendedKeywords(selectedKeywords: List<KeywordModel>) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
        for (keyword in selectedKeywords) {
            myRef.push().setValue(KeywordModel(keyword.keyword, "", false, keyword.imageUrl))
        }
        dismiss()
    }

    // 키워드 추천 후 추천 키워드를 다이얼로그로 표시하는 메서드 추가
    private fun onRecommendedKeywordsFetched(recommendations: List<KeywordModel>) {
        recommendedKeywords = recommendations

        // 추천 키워드를 버튼 형식으로 표시
        val buttonLayout = binding.buttonLayout // 버튼을 추가할 레이아웃

        // 기존 버튼 제거
        buttonLayout.removeAllViews()

        recommendations.forEach { keywordModel ->
            val keyword = keywordModel.keyword
            val button = Button(context).apply {
                text = keyword
                setOnClickListener {
                    if (selectedKeywords.contains(keywordModel)) {
                        selectedKeywords.remove(keywordModel) // 이미 선택된 키워드라면 제거
                        setBackgroundColor(Color.LTGRAY) // 선택 해제 시 색상 변경
                    } else {
                        selectedKeywords.add(keywordModel) // 선택된 키워드 추가
                        setBackgroundColor(Color.parseColor("#0064FF")) // 선택 시 색상 변경
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
