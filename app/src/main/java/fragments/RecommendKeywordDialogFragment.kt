package fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentRecommendedKeywordsDialogBinding
import android.widget.Toast
import android.widget.Button
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firestore.v1.Document
import com.unit_3.sogong_test.KeywordModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import com.unit_3.sogong_test.BuildConfig
import org.json.JSONException
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class RecommendedKeywordsDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentRecommendedKeywordsDialogBinding
    private var onKeywordsSelected: ((List<KeywordModel>) -> Unit)? = null
    private val selectedKeywords = mutableListOf<KeywordModel>()
    private lateinit var keyword: String

    companion object {
        fun newInstance(
            keyword: String,
            onKeywordsSelected: (List<KeywordModel>) -> Unit
        ): RecommendedKeywordsDialogFragment {
            val fragment = RecommendedKeywordsDialogFragment()
            fragment.keyword = keyword
            fragment.onKeywordsSelected = onKeywordsSelected
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_recommended_keywords_dialog,
            null,
            false
        )

        // 다이얼로그 레이아웃 설정
        val dialogView = binding.root

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("확인", null) // 클릭 이벤트 처리 없음
            .setNegativeButton("취소", null) // 클릭 이벤트 처리 없음
            .create()

        dialog.setOnShowListener {
            // 다이얼로그가 화면에 표시된 후 버튼 색상 변경
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton?.setTextColor(Color.parseColor("#4169E1")) // 확인 버튼 색상
            negativeButton?.setTextColor(Color.parseColor("#ff0000")) // 취소 버튼 색상
        }

        // 데이터 로딩
        fetchRecommendedKeywords(keyword)

        return dialog
    }

    private fun fetchRecommendedKeywords(keyword: String) {
        val client = OkHttpClient()
        val requestBodyJson = JSONObject().apply {
            put("model", "gpt-4o")
            put(
                "messages",
                JSONArray().put(
                    JSONObject().put("role", "user").put(
                        "content",
                        "{$keyword}와 관련된 인기 검색어를 받고 싶어. 최근 한달간 미디어에서 핫했던 3개의 연관 키워드를 추천해줬으면 좋겠어. JSON 배열 형식으로, 각 키워드를 큰따옴표로 감싸고 쉼표로 구분하여 응답을 보내줄래?"
                    )
                )
            )
            put("max_tokens", 50)
            put("temperature", 0.7)
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
                Log.e("RecommendedKeywordsDialogFragment", "Failed to fetch recommended keywords", e)
                activity?.runOnUiThread {
                    binding.loadingMessage.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("RecommendedKeywordsDialogFragment", "Unexpected code $it")
                        return
                    }

                    val responseBody = it.body!!.string()
                    Log.d("API Response", responseBody)

                    val keywords = extractKeywordsFromResponse(responseBody)

                    if (keywords.isNullOrEmpty()) {
                        Log.e("RecommendedKeywordsDialogFragment", "No keywords found in response")
                        return
                    }

                    val keywordModels = mutableListOf<KeywordModel>()
                    var remainingKeywords = keywords.size

                    keywords.forEach { keyword ->
                        fetchImageUrlFromGoogle(keyword, { imageUrl ->
                            keywordModels.add(KeywordModel(keyword, "", false, imageUrl))
                            remainingKeywords--

                            if (remainingKeywords == 0 && isAdded) {
                                activity?.runOnUiThread {
                                    displayRecommendedKeywords(keywordModels)
                                }
                            }
                        }, { error ->
                            keywordModels.add(KeywordModel(keyword, "", false, ""))
                            remainingKeywords--

                            if (remainingKeywords == 0 && isAdded) {
                                activity?.runOnUiThread {
                                    displayRecommendedKeywords(keywordModels)
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    private fun extractKeywordsFromResponse(response: String): List<String>? {
        return try {
            val jsonObject = JSONObject(response)
            val choicesArray = jsonObject.getJSONArray("choices")

            if (choicesArray.length() > 0) {
                val messageContent = choicesArray.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                val jsonArrayString = messageContent
                    .trim()
                    .removePrefix("```json\n")
                    .removeSuffix("\n```")

                val jsonArray = JSONArray(jsonArrayString)
                List(jsonArray.length()) { jsonArray.getString(it).trim() }
            } else {
                null
            }
        } catch (e: JSONException) {
            Log.e("RecommendedKeywordsDialogFragment", "Error parsing response", e)
            null
        }
    }

    private fun fetchImageUrlFromGoogle(
        query: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val encodedQuery = query.replace(" ", "+")
        val url = "https://www.google.com/search?hl=en&tbm=isch&q=$encodedQuery"

        try {
            val document: org.jsoup.nodes.Document = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
            val imageElements: Elements = document.select("img")

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

    private fun displayRecommendedKeywords(recommendations: List<KeywordModel>) {
        val buttonLayout = binding.buttonLayout
        binding.loadingMessage.visibility = View.GONE
        binding.keywordsListView.visibility = View.VISIBLE
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 32) // 아래쪽에 4dp 여백 추가
        }

        recommendations.forEach { keywordModel ->
            val keyword = keywordModel.keyword
            val button = Button(context).apply {
                text = keyword
                setBackgroundResource(R.drawable.rounded_button_background)
                setTextColor(Color.parseColor("#2E2E2E"))
                layoutParams = params // 여백 적용

                setOnClickListener {
                    if (selectedKeywords.contains(keywordModel)) {
                        selectedKeywords.remove(keywordModel)
                        setBackgroundResource(R.drawable.rounded_button_background)
                        setTextColor(Color.parseColor("#2E2E2E"))
                    } else {
                        selectedKeywords.add(keywordModel)
                        val selectedBackground = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 50f
                            setColor(Color.parseColor("#0064FF"))
                        }
                        background = selectedBackground
                        setTextColor(Color.WHITE)
                    }
                }
            }
            buttonLayout.addView(button)
        }
    }
}
