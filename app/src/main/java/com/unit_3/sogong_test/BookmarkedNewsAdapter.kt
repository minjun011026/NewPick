import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unit_3.sogong_test.BookmarkedNewsModel
import com.unit_3.sogong_test.R

class BookmarkedNewsAdapter(private val newsItems: ArrayList<BookmarkedNewsModel>) :
    RecyclerView.Adapter<BookmarkedNewsAdapter.ViewHolder>() {

    private var itemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmarked_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsItems[position])
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BookmarkedNewsModel) {
            val newsTitleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
            newsTitleTextView.text = item.title

            // Handle item click
            itemView.setOnClickListener {
                itemClickListener?.invoke(adapterPosition)
            }
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        itemClickListener = listener
    }
}
