package fragments

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.unit_3.sogong_test.KeywordModel
import com.unit_3.sogong_test.R
import com.unit_3.sogong_test.databinding.FragmentAddKeywordDialogBinding


class AddKeywordDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddKeywordDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_keyword_dialog, container, false)

        binding.registerBtn.setOnClickListener {
            val keyword = binding.keywordArea.text

            if(keyword != null){
                val database = Firebase.database
                val myRef = database.getReference("keyword").child(Firebase.auth.currentUser!!.uid)
                myRef.push().setValue(KeywordModel(keyword.toString()))
                dismiss()
            }
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }


}