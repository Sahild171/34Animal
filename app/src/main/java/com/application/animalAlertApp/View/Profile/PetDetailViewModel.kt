package com.application.animalAlertApp.View.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.View.AddAlerts.MyPetsState
import com.application.animalAlertApp.data.Repositry.PetDetailRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PetDetailViewModel @Inject constructor(val repositry:PetDetailRepositry):ViewModel(){

    private val _mypetData: MutableLiveData<MyPetsState> = MutableLiveData()
    val mypetData: LiveData<MyPetsState> = _mypetData

    fun getpets()= viewModelScope.launch {
        repositry.getPet("").onStart {
            _mypetData.value = MyPetsState.Loading
        }.catch { e ->
            _mypetData.value = MyPetsState.Failure(e)
        }.collect { response ->
            _mypetData.value = MyPetsState.Success(response)
        }
    }

}