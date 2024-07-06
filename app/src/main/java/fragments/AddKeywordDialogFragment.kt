package fragments

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.*
import androidx.fragment.app.DialogFragment
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentAddKeywordDialogBinding


class AddKeywordDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddKeywordDialogBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_keyword_dialog, container, false)

        binding.registerBtn.setOnClickListener {
            val keyword = binding.keywordArea.text.toString()

            //keyword를 firebase database에 저장해주는 작업이 요구됨.




            dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }


}