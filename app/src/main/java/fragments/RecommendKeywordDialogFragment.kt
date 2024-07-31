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
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import com.unit_3.sogong_test.KeywordModel

class RecommendedKeywordsDialogFragment : DialogFragment() {

    private lateinit var recommendations: List<KeywordModel>
    private var onKeywordsSelected: ((List<KeywordModel>) -> Unit)? = null
    private val selectedKeywords = mutableListOf<KeywordModel>()

    companion object {
        fun newInstance(
            recommendations: List<KeywordModel>,
            onKeywordsSelected: (List<KeywordModel>) -> Unit
        ): RecommendedKeywordsDialogFragment {
            val fragment = RecommendedKeywordsDialogFragment()
            fragment.recommendations = recommendations
            fragment.onKeywordsSelected = onKeywordsSelected
            return fragment
        }
    }

    private fun dismissAddKeywordDialog() {
        // 키워드 등록 다이얼로그를 종료하는 로직을 추가합니다.
        val addKeywordDialog =
            childFragmentManager.findFragmentByTag("AddKeywordDialogFragment") as? AddKeywordDialogFragment
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
        val dialogView =
            layoutInflater.inflate(R.layout.fragment_recommended_keywords_dialog, null)
        val buttonLayout = dialogView.findViewById<LinearLayout>(R.id.buttonLayout)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 16) // 아래쪽에 4dp 여백 추가
        }

        recommendations.forEach { keywordModel ->
            val keyword = keywordModel.keyword
            val button = Button(context).apply {
                text = keyword
                setBackgroundResource(R.drawable.rounded_button_background) // 둥근 배경 설정
                setTextColor(Color.BLACK) // 기본 글자색 설정
                layoutParams = params // 여백 적용

                setOnClickListener {
                    if (selectedKeywords.contains(keywordModel)) {
                        selectedKeywords.remove(keywordModel)
                        setBackgroundResource(R.drawable.rounded_button_background) // 선택 해제 시 둥근 배경으로 설정
                        setTextColor(Color.BLACK) // 선택 해제 시 글자색 변경
                    } else {
                        selectedKeywords.add(keywordModel)
                        // 선택 시 배경을 변경하기 위해 Drawable을 변경
                        val selectedBackground = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 50f // 모서리 둥글
                            setColor(Color.parseColor("#0064FF")) // 선택 시 색상 변경
                        }
                        background = selectedBackground // 배경 변경
                        setTextColor(Color.WHITE) // 선택 시 글자색 변경
                    }
                }
            }
            buttonLayout.addView(button)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                onKeywordsSelected?.invoke(selectedKeywords)
            }
            .setNegativeButton("취소", null)

        return dialog.create()
    }
}
