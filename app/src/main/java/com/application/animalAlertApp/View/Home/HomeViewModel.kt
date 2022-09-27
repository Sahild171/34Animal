package com.application.animalAlertApp.View.Home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.HomeRepositry
import com.application.animalAlertApp.data.Response.Post.GetAllPosts
import com.application.animalAlertApp.data.Response.Post.PostLikeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(val repositry: HomeRepositry) : ViewModel() {

    private val _mypostdata: MutableLiveData<Resource<GetAllPosts>> = MutableLiveData()
    val mypostdata: LiveData<Resource<GetAllPosts>> = _mypostdata


    private val _firstpostdata: MutableLiveData<Resource<GetAllPosts>> = MutableLiveData()
    val firstpostdata: LiveData<Resource<GetAllPosts>> = _firstpostdata

    private val _mypostlikedata: MutableLiveData<Resource<PostLikeResponse>> = MutableLiveData()
    val mypostlikedata: LiveData<Resource<PostLikeResponse>> = _mypostlikedata


    fun getAllPosts(page: Int) = viewModelScope.launch {
        repositry.getAllPosts(page).onStart {
            _mypostdata.value = Resource.Loading
        }.catch { e ->
            _mypostdata.value = Resource.Failure(e)
        }.collect { response ->
            _mypostdata.value = Resource.Success(response)
        }
    }

    fun getfirstPosts(page: Int) = viewModelScope.launch {
        repositry.getAllPosts(page).onStart {
            _firstpostdata.value = Resource.Loading
        }.catch { e ->
            _firstpostdata.value = Resource.Failure(e)
        }.collect { response ->
            _firstpostdata.value = Resource.Success(response)
        }
    }
    fun postlike(postId: String) = viewModelScope.launch {
        repositry.likepost(postId).onStart {
            _mypostlikedata.value = Resource.Loading
        }.catch { e ->
            _mypostlikedata.value = Resource.Failure(e)
        }.collect { response ->
            _mypostlikedata.value = Resource.Success(response)
        }
    }


}