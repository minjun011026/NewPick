package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentRecommendedKeywordsDialogBinding

class RecommendedKeywordsDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentRecommendedKeywordsDialogBinding
    private var selectedKeywords = mutableListOf<String>()

    companion object {
        private const val ARG_KEYWORDS = "keywords"

        fun newInstance(keywords: List<String>, onKeywordsSelected: (List<String>) -> Unit): RecommendedKeywordsDialogFragment {
            val fragment = RecommendedKeywordsDialogFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_KEYWORDS, ArrayList(keywords))
            fragment.arguments = args
            fragment.onKeywordsSelected = onKeywordsSelected
            return fragment
        }
    }

    private var onKeywordsSelected: ((List<String>) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecommendedKeywordsDialogBinding.inflate(inflater, container, false)

        val keywords = arguments?.getStringArrayList(ARG_KEYWORDS) ?: emptyList()
        binding.keywordsTextView.text = keywords.joinToString("\n")

        binding.completeBtn.setOnClickListener {
            onKeywordsSelected?.invoke(selectedKeywords)
            dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        // 키워드 선택 로직 추가

        return binding.root
    }


}