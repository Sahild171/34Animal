package com.application.animalAlertApp.View.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.View.AddAlerts.MyPetsState
import com.application.animalAlertApp.data.Repositry.MyProfileRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AllPetViewModel @Inject constructor(val repositry: MyProfileRepositry): ViewModel() {
    private val _mypetData: MutableLiveData<MyPetsState> = MutableLiveData()
    val mypetData: LiveData<MyPetsState> = _mypetData


    //DeltePet
    private val _deletePetdata: MutableLiveData<Resource<GenricResponse>> =
        MutableLiveData()
    val deletePetdata: LiveData<Resource<GenricResponse>> = _deletePetdata


    fun getpets()= viewModelScope.launch {
        repositry.getmyPets().onStart {
            _mypetData.value = MyPetsState.Loading
        }.catch { e ->
            _mypetData.value = MyPetsState.Failure(e)
        }.collect { response ->
            _mypetData.value = MyPetsState.Success(response)
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

}