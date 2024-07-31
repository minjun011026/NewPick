package fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentRecommendedKeywordsDialogBinding
import android.widget.Toast
import android.widget.Button
import android.graphics.Color
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.unit_3.sogong_test.KeywordModel


//class RecommendedKeywordsDialogFragment : DialogFragment() {
//    private lateinit var binding: FragmentRecommendedKeywordsDialogBinding
//    private var selectedKeywords = mutableListOf<String>()
//
//    companion object {
//        private const val ARG_KEYWORDS = "keywords"
//
//        fun newInstance(keywords: List<String>, onKeywordsSelected: (List<String>) -> Unit): RecommendedKeywordsDialogFragment {
//            val fragment = RecommendedKeywordsDialogFragment()
//            val args = Bundle()
//            args.putStringArrayList(ARG_KEYWORDS, ArrayList(keywords))
//            fragment.arguments = args
//            fragment.onKeywordsSelected = onKeywordsSelected
//            return fragment
//        }
//    }
//
//    private var onKeywordsSelected: ((List<String>) -> Unit)? = null
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        binding = FragmentRecommendedKeywordsDialogBinding.inflate(inflater, container, false)
//
//        val keywords = arguments?.getStringArrayList(ARG_KEYWORDS) ?: emptyList()
//        setupKeywordButtons(keywords)
//
//        binding.completeBtn.setOnClickListener {
//            saveRecommendedKeywords(selectedKeywords) // 키워드 저장 호출
//            onKeywordsSelected?.invoke(selectedKeywords)
//            dismiss()
//        }
//
//        binding.cancelBtn.setOnClickListener {
//            dismiss()
//        }
//
//        return binding.root
//    }
//
//    private fun setupKeywordButtons(keywords: List<String>) {
//        val buttonLayout = binding.buttonLayout // 버튼을 추가할 레이아웃
//
//        // 기존 버튼 제거
//        buttonLayout.removeAllViews()
//
//        keywords.forEach { keyword ->
//            val button = Button(context).apply {
//                text = keyword
//                setOnClickListener {
//                    if (selectedKeywords.contains(keyword)) {
//                        selectedKeywords.remove(keyword) // 이미 선택된 키워드라면 제거
//                        setBackgroundColor(Color.LTGRAY) // 선택 해제 시 색상 변경
//                    } else {
//                        selectedKeywords.add(keyword) // 선택된 키워드 추가
//                        setBackgroundColor(Color.GREEN) // 선택 시 색상 변경
//                    }
//                }
//            }
//            buttonLayout.addView(button)
//        }
//    }
//
//    private fun saveRecommendedKeywords(selectedKeywords: List<String>) {
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("keyword").child(FirebaseAuth.getInstance().currentUser!!.uid)
//        for (keyword in selectedKeywords) {
//            myRef.push().setValue(KeywordModel(keyword))
//        }
//    }
//}

class RecommendedKeywordsDialogFragment : DialogFragment() {

    private lateinit var recommendations: List<String>
    private var onKeywordsSelected: ((List<String>) -> Unit)? = null
    private val selectedKeywords = mutableListOf<String>()

    companion object {
        fun newInstance(recommendations: List<String>, onKeywordsSelected: (List<String>) -> Unit): RecommendedKeywordsDialogFragment {
            val fragment = RecommendedKeywordsDialogFragment()
            fragment.recommendations = recommendations
            fragment.onKeywordsSelected = onKeywordsSelected
            return fragment
        }
    }
    private fun dismissAddKeywordDialog() {
        // 키워드 등록 다이얼로그를 종료하는 로직을 추가합니다.
        val addKeywordDialog = childFragmentManager.findFragmentByTag("AddKeywordDialogFragment") as? AddKeywordDialogFragment
        addKeywordDialog?.dismiss()
    }
    private fun onCompleteButtonClick() {
        if (selectedKeywords.isNotEmpty()) {
            onKeywordsSelected?.invoke(selectedKeywords)
            dismiss() // 추천 키워드 다이얼로그 종료
            dismissAddKeywordDialog() // 키워드 등록 다이얼로그 종료
        } else {
            Toast.makeText(context, "키워드를 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
        val buttonLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }

        recommendations.forEach { keyword ->
            val button = Button(context).apply {
                text = keyword
                setOnClickListener {
                    if (selectedKeywords.contains(keyword)) {
                        selectedKeywords.remove(keyword)
                        setBackgroundColor(Color.LTGRAY) // 선택 해제 시 색상 변경
                    } else {
                        selectedKeywords.add(keyword)
                        setBackgroundColor(Color.GREEN) // 선택 시 색상 변경
                    }
                }
            }
            buttonLayout.addView(button)
        }

        val completeButton = Button(context).apply {
            text = "완료"
            setOnClickListener {
                onCompleteButtonClick() // 수정한 메서드 호출
            }
        }

        buttonLayout.addView(completeButton)

        dialog.setView(buttonLayout)
        return dialog.create()
    }
}
