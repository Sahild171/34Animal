package com.application.animalAlertApp.View.Friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.FriendsRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Profile.SearchUserResponse
import com.application.animalAlertApp.data.Response.Request.GetfriendRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendsViewModel @Inject constructor(val repositry: FriendsRepositry) : ViewModel() {
    private val _frienddata: MutableLiveData<Resource<GetfriendRequests>> = MutableLiveData()
    val frienddata: LiveData<Resource<GetfriendRequests>> = _frienddata

    private val _removefrienddata: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val removefrienddata: LiveData<Resource<GenricResponse>> = _removefrienddata

    //SearchUser
    private val _searchuser: MutableLiveData<Resource<SearchUserResponse>> = MutableLiveData()
    val searchuser: LiveData<Resource<SearchUserResponse>> = _searchuser

    fun getfirends(status: String) = viewModelScope.launch {
        repositry.getfirends(status).onStart {
            _frienddata.value = Resource.Loading
        }.catch { e ->
            _frienddata.value = Resource.Failure(e)
        }.collect { response ->
            _frienddata.value = Resource.Success(response)
        }
    }


    fun removefriends(friendId: String) = viewModelScope.launch {
        repositry.removefreinds(friendId).onStart {
            _removefrienddata.value = Resource.Loading
        }.catch { e ->
            _removefrienddata.value = Resource.Failure(e)
        }.collect { response ->
            _removefrienddata.value = Resource.Success(response)
        }
    }

    fun searchuser(name:String)= viewModelScope.launch {
        repositry.searchuser(name).onStart {
            _searchuser.value = Resource.Loading
        }.catch { e ->
            _searchuser.value = Resource.Failure(e)
        }.collect { response ->
            _searchuser.value = Resource.Success(response)
        }
    }

}