import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.unit_3.sogong_test.KeywordModel
import com.unit_3.sogong_test.KeywordNewsActivity
import com.unit_3.sogong_test.R

class KeywordRVAdapter(private val items: ArrayList<KeywordModel>) :
    RecyclerView.Adapter<KeywordRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.keyword_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: KeywordModel) {
            val keywordArea = itemView.findViewById<TextView>(R.id.keywordArea)
            val moreVertButton = itemView.findViewById<ImageButton>(R.id.moreVertBtn)

            keywordArea.text = item.keyword

            // 키워드 클릭 시 처리
            keywordArea.setOnClickListener {
                Toast.makeText(itemView.context, "키워드 클릭: ${item.keyword}", Toast.LENGTH_LONG).show()

                val intent = Intent(itemView.context, KeywordNewsActivity::class.java)
                intent.putExtra("키워드", item.keyword)
                itemView.context.startActivity(intent)
            }

            // 더 보기 버튼 처리
            moreVertButton.setOnClickListener {
                showPopupMenu(moreVertButton, item)
            }
        }

        private fun showPopupMenu(view: View, item: KeywordModel) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        // 삭제 로직 추가
                        deleteKeyword(item)
                        true
                    }
                    R.id.action_bell -> {
                        Toast.makeText(itemView.context, "Bell 클릭: ${item.keyword}", Toast.LENGTH_SHORT).show()
                        // 여기에 Bell 버튼 클릭 시 처리할 로직 추가
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        private fun deleteKeyword(item: KeywordModel) {
            // RecyclerView에서 해당 아이템 삭제
            val position = items.indexOf(item)
            items.remove(item)
            notifyItemRemoved(position)

            // 여기에 추가적으로 데이터베이스나 파일 시스템에서도 삭제하는 로직을 추가할 수 있습니다.

            //  Firebase Realtime Database에서 키워드 삭제
            val database = Firebase.database
            val myRef = database.getReference("keyword").child(Firebase.auth.currentUser!!.uid)
            myRef.child(item.id).removeValue()


            // 삭제 성공 메시지
            Toast.makeText(itemView.context, "${item.keyword} 삭제 완료", Toast.LENGTH_SHORT).show()
        }
    }

}
