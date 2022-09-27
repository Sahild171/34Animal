package com.application.animalAlertApp.View.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.View.AddAlerts.MyPetsState
import com.application.animalAlertApp.View.Post.MyPostState
import com.application.animalAlertApp.data.Repositry.MyProfileRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.ShopStatusResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyProfileViewModel @Inject constructor(val repositry: MyProfileRepositry) : ViewModel() {
    /////getmypets
    private val _mypetData: MutableLiveData<MyPetsState> = MutableLiveData()
    val mypetData: LiveData<MyPetsState> = _mypetData

    //MyPostData
    private val _mypostData: MutableLiveData<MyPostState> = MutableLiveData()
    val mypostData: LiveData<MyPostState> = _mypostData

    //DeltePet
    private val _deletePetdata: MutableLiveData<Resource<GenricResponse>> =
        MutableLiveData()
    val deletePetdata: LiveData<Resource<GenricResponse>> = _deletePetdata


    private val _checkshopdata: MutableLiveData<Resource<ShopStatusResponse>> = MutableLiveData()
    val checkshopdata: LiveData<Resource<ShopStatusResponse>> = _checkshopdata




    fun getpets() = viewModelScope.launch {
        repositry.getmyPets().onStart {
            _mypetData.value = MyPetsState.Loading
        }.catch { e ->
            _mypetData.value = MyPetsState.Failure(e)
        }.collect { response ->
            _mypetData.value = MyPetsState.Success(response)
        }
    }


    fun getmyposts() = viewModelScope.launch {
        repositry.getmyPost().onStart {
            _mypostData.value = MyPostState.Loading
        }.catch { e ->
            _mypostData.value = MyPostState.Failure(e)
        }.collect { response ->
            _mypostData.value = MyPostState.Success(response)
        }
    }

    fun deletepet(petid: String) = viewModelScope.launch {
        repositry.deletepet(petid).onStart {
            _deletePetdata.value = Resource.Loading
        }.catch { e ->
            _deletePetdata.value = Resource.Failure(e)
        }.collect { response ->
            _deletePetdata.value = Resource.Success(response)
        }
    }


    fun checkshopdata() = viewModelScope.launch {
        repositry.getstatuscheck().onStart {
            _checkshopdata.value = Resource.Loading
        }.catch { e ->
            _checkshopdata.value = Resource.Failure(e)
        }.collect { response ->
            _checkshopdata.value = Resource.Success(response)
        }
    }




}