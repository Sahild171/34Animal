package com.application.animalAlertApp.View.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.View.Post.MyPostState
import com.application.animalAlertApp.data.Repositry.MyProfileRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyAllPostViewModel @Inject constructor(val repositry: MyProfileRepositry):ViewModel() {
    private val _mypostData: MutableLiveData<MyPostState> = MutableLiveData()
    val mypostData: LiveData<MyPostState> = _mypostData


    fun getmyposts()= viewModelScope.launch {
        repositry.getmyPost().onStart {
            _mypostData.value = MyPostState.Loading
        }.catch { e ->
            _mypostData.value = MyPostState.Failure(e)
        }.collect { response ->
            _mypostData.value = MyPostState.Success(response)
        }
    }

}