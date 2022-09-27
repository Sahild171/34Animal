package com.application.animalAlertApp.View.BusinessProfile.EditBussProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.EditBusinessRepositry
import com.application.animalAlertApp.data.Response.BusinessProfile.EditBusinessResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBusinessViewModel @Inject constructor(val repositry: EditBusinessRepositry) :
    ViewModel() {

    private val _editbussiness: MutableLiveData<Resource<EditBusinessResponse>> = MutableLiveData()
    val editbussiness: LiveData<Resource<EditBusinessResponse>> = _editbussiness


    fun editbusiness(
        businessName: String,
        businessDescription: String,
        mobileNo: String,
        location: String
    ) = viewModelScope.launch {
        repositry.editbusiness(businessName, businessDescription, mobileNo, location).onStart {
            _editbussiness.value = Resource.Loading
        }.catch { e ->
            _editbussiness.value = Resource.Failure(e)
        }.collect { response ->
            _editbussiness.value = Resource.Success(response)
        }
    }


}