package com.application.animalAlertApp.View.Home.Comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.CommentRepositry
import com.application.animalAlertApp.data.Response.Comments.AddComment
import com.application.animalAlertApp.data.Response.Comments.CommentReplyresponse
import com.application.animalAlertApp.data.Response.Comments.GetCommentsResponse
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CommentsViewModel @Inject constructor(val repositry: CommentRepositry) : ViewModel() {
    ////GetAllCommentsData
    private val _mycommentdata: MutableLiveData<Resource<GetCommentsResponse>> = MutableLiveData()
    val mycommentdata: LiveData<Resource<GetCommentsResponse>> = _mycommentdata

    //////AddCommentsData
    private val _addcommentdata: MutableLiveData<Resource<AddComment>> = MutableLiveData()
    val addcommentdata: LiveData<Resource<AddComment>> = _addcommentdata

    private val _replydata: MutableLiveData<Resource<CommentReplyresponse>> = MutableLiveData()
    val replydata: LiveData<Resource<CommentReplyresponse>> = _replydata


    private val _likedata: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val likedata: LiveData<Resource<GenricResponse>> = _likedata


    fun getAllcomments(postid: String) = viewModelScope.launch {
        repositry.getcomments(postid).onStart {
            _mycommentdata.value = Resource.Loading
        }.catch { e ->
            _mycommentdata.value = Resource.Failure(e)
        }.collect { response ->
            _mycommentdata.value = Resource.Success(response)
        }
    }

    fun addcommnet(postid: String, comment: String) = viewModelScope.launch {
        repositry.addcomment(postid, comment).onStart {
            _addcommentdata.value = Resource.Loading
        }.catch { e ->
            _addcommentdata.value = Resource.Failure(e)
        }.collect { response ->
            _addcommentdata.value = Resource.Success(response)
        }
    }


    fun replycomment(commentId: String, reply: String) = viewModelScope.launch {
        repositry.replycomment(commentId, reply).onStart {
            _replydata.value = Resource.Loading
        }.catch { e ->
            _replydata.value = Resource.Failure(e)
        }.collect { response ->
            _replydata.value = Resource.Success(response)
        }
    }


    fun likecomment(commentId: String) = viewModelScope.launch {
        repositry.likecomment(commentId).onStart {
            _likedata.value = Resource.Loading
        }.catch { e ->
            _likedata.value = Resource.Failure(e)
        }.collect { response ->
            _likedata.value = Resource.Success(response)
        }
    }


}