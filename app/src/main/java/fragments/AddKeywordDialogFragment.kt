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
import android.util.Log

class AddKeywordDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddKeywordDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_keyword_dialog,
            container,
            false
        )

        binding.registerBtn.setOnClickListener {
            val keyword = binding.keywordArea.text.toString()

            if (keyword.isNotEmpty()) {
                thread {
                    fetchImageUrlFromGoogle(keyword, { imageUrl ->
                        activity?.runOnUiThread {
                            saveKeywordToFirebase(keyword, imageUrl)
                        }
                    }, { error ->
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

    private fun fetchImageUrlFromGoogle(
        query: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val encodedQuery = query.replace(" ", "+")
        val url = "https://www.google.com/search?hl=en&tbm=isch&q=$encodedQuery"

        try {
            val document: Document = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
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

    private fun saveKeywordToFirebase(keyword: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef =
            database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
        myRef.push().setValue(KeywordModel(keyword, "", false, imageUrl))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 첫 번째 다이얼로그 닫기
                    dismiss()
                    // 두 번째 다이얼로그 열기
                    showRecommendedKeywordsDialog(keyword)
                } else {
                    Log.e("AddKeywordDialogFragment", "Failed to save keyword to Firebase")
                }
            }
    }

    private fun showRecommendedKeywordsDialog(keyword: String) {
        val dialog = RecommendedKeywordsDialogFragment.newInstance(keyword) { selectedKeywords ->
            saveRecommendedKeywords(selectedKeywords) // 선택된 키워드 저장
        }
        dialog.show(parentFragmentManager, "RecommendedKeywordsDialog")
    }

    private fun saveRecommendedKeywords(selectedKeywords: List<KeywordModel>) {
        val database = FirebaseDatabase.getInstance()
        val myRef =
            database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
        for (keyword in selectedKeywords) {
            myRef.push().setValue(KeywordModel(keyword.keyword, "", false, keyword.imageUrl))
        }
    }
}
