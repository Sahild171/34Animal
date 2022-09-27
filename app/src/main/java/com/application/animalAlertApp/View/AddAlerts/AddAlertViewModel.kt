package com.application.animalAlertApp.View.AddAlerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.data.Repositry.AlertsRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddAlertViewModel @Inject constructor(val repositry: AlertsRepositry): ViewModel(){
    /////getmypets
    private val _mypetData: MutableStateFlow<MyPetsState> = MutableStateFlow(MyPetsState.Empty)
    val mypetData: StateFlow<MyPetsState> = _mypetData


    fun addalert(
        addtitle: String,
        prioroty: String,
        description:String,
        Status:String,
        petId:String?
    ) = repositry.addalert(addtitle,prioroty,description,Status,petId)



    fun getpets()= viewModelScope.launch {
        repositry.getmyPets().onStart {
            _mypetData.value = MyPetsState.Loading
        }.catch { e ->
            _mypetData.value = MyPetsState.Failure(e)
        }.collect { response ->
            _mypetData.value = MyPetsState.Success(response)
        }
    }


    fun editalert(
        alertid: String,
        addtitle: String,
        petId:String?,
        priority:String,
        description:String,
    ) = repositry.editalert(alertid,addtitle,petId
        ,priority,description)
}