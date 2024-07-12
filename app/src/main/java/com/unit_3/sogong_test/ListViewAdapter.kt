package com.unit_3.sogong_test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.Toast

class ListViewAdapter (val context : Context,
                       val list : ArrayList<String>,
                       val bottomSheetDialog: BottomSheetDialog) : BaseAdapter()
{
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.layout_listview_item, null)
        val textView = view.findViewById<TextView>(R.id.textView)
        val item = list[p0]
        textView.setText(item)

        textView.setOnClickListener{
            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
        }

        return view
    }
}