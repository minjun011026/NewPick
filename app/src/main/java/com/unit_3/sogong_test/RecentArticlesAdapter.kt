package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.unit_3.sogong_test.databinding.ItemArticleBinding

class RecentArticlesAdapter :
    ListAdapter<ArticleModel, RecentArticlesAdapter.ArticleViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val newsItem = getItem(position)
        holder.bind(newsItem)
    }

    class ArticleViewHolder(private val binding: ItemArticleBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(newsItem: ArticleModel) {
            binding.titleTextView.text = newsItem.title
            binding.descriptionTextView.text = newsItem.description

            // 이미지 로딩
            if (newsItem.imageResId != 0) {
                binding.newsImageView.setImageResource(newsItem.imageResId)
            } else {
                Glide.with(binding.newsImageView.context)
                    .load(newsItem.imageUrl)
                    .into(binding.newsImageView)
            }

            binding.root.setOnClickListener {
                NewsStorage.saveNews(it.context, newsItem)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ArticleModel>() {
        override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
            return oldItem == newItem
        }
    }
}
