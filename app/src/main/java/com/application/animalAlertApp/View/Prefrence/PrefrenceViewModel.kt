package com.application.animalAlertApp.View.Prefrence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.PrefrenceRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PrefrenceViewModel @Inject constructor(val repositry: PrefrenceRepositry) : ViewModel() {
    private val _changeprefrence: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val changeprefrence: LiveData<Resource<GenricResponse>> = _changeprefrence



    fun saveprefrence(
        user_id: String,
        perference: String
    ) = repositry.saveprefrence(user_id, perference)





    fun changePrfrences(perference: String) = viewModelScope.launch {
        repositry.changePrfrences(perference).onStart {
            _changeprefrence.value = Resource.Loading
        }.catch { e ->
            _changeprefrence.value = Resource.Failure(e)
        }.collect { response ->
            _changeprefrence.value = Resource.Success(response)
        }
    }


}