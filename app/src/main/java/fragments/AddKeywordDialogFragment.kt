package fragments

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

class AddKeywordDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddKeywordDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
        val keywordModel = KeywordModel(keyword = keyword, imageUrl = imageUrl)
        myRef.push().setValue(keywordModel)
            .addOnSuccessListener {
                // 데이터 저장 성공
                dismiss()
            }
            .addOnFailureListener { error ->
                // 데이터 저장 실패 처리
                error.printStackTrace()
            }
    }
}
