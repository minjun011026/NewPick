package com.unit_3.sogong_test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.unit_3.sogong_test.ApiSearchNews
import com.unit_3.sogong_test.KeywordNewsModel

class MapNewsViewModel : ViewModel() {

    private val _newsItems = MutableLiveData<List<KeywordNewsModel>>()
    val newsItems: LiveData<List<KeywordNewsModel>> get() = _newsItems

    private val _dataLoaded = MutableLiveData<Boolean>()
    val dataLoaded: LiveData<Boolean> get() = _dataLoaded

    fun fetchNews(city: String) {
        viewModelScope.launch {
            _dataLoaded.value = false
            val fetchedNewsItems = withContext(Dispatchers.IO) {
                if (city.isNotEmpty()) {
                    ApiSearchNews.main(city)
                } else {
                    emptyList()
                }
            }
            _newsItems.postValue(fetchedNewsItems)
            _dataLoaded.value = true
        }
    }
}
