package com.unit_3.sogong_test

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NewsStorage {
    private const val PREFS_NAME = "news_prefs"
    private const val KEY_RECENT_NEWS = "recent_news"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveNews(context: Context, newsItem: ArticleModel) {
        val prefs = getPreferences(context)
        val gson = Gson()
        val recentNewsJson = prefs.getString(KEY_RECENT_NEWS, "[]")
        val type = object : TypeToken<MutableList<ArticleModel>>() {}.type
        val recentNews: MutableList<ArticleModel> = gson.fromJson(recentNewsJson, type) ?: mutableListOf()

        // 중복 체크 및 추가
        if (recentNews.none { it.id == newsItem.id }) {
            recentNews.add(newsItem)
        }

        val updatedNewsJson = gson.toJson(recentNews)
        with(prefs.edit()) {
            putString(KEY_RECENT_NEWS, updatedNewsJson)
            apply()
        }
    }

    fun getRecentNews(context: Context): List<ArticleModel> {
        val prefs = getPreferences(context)
        val gson = Gson()
        val recentNewsJson = prefs.getString(KEY_RECENT_NEWS, "[]")
        val type = object : TypeToken<List<ArticleModel>>() {}.type
        return gson.fromJson(recentNewsJson, type) ?: emptyList()
    }
}
