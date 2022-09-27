package com.application.animalAlertApp.View.Pet


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.AddPetRepositry
import com.application.animalAlertApp.data.Response.AddPetResponse
import com.application.animalAlertApp.data.Response.EditPetImages
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class AddPetViewModel @Inject constructor(private val repositry: AddPetRepositry) : ViewModel() {
    private val _petData: MutableStateFlow<PetTypeState> = MutableStateFlow(PetTypeState.Empty)
    val petData: StateFlow<PetTypeState> = _petData

    //DeltePetimage
    private val _deletePetimage: MutableLiveData<Resource<GenricResponse>> =
        MutableLiveData()
    val deletePetimage: LiveData<Resource<GenricResponse>> = _deletePetimage


    //Editpetdetail
    private val _editpetdata: MutableLiveData<Resource<GenricResponse>> =
        MutableLiveData()
    val editpetdata: LiveData<Resource<GenricResponse>> = _editpetdata
    //EditpetImages
    private val _editpetimage: MutableLiveData<Resource<EditPetImages>> =
        MutableLiveData()
    val editpetimage: LiveData<Resource<EditPetImages>> = _editpetimage

    //EditpetImages
    private val _petprofileimage: MutableLiveData<Resource<GenricResponse>> =
        MutableLiveData()
    val petprofileimage: LiveData<Resource<GenricResponse>> = _petprofileimage

    fun addpet(
        petname: RequestBody,
        petType: RequestBody,
        petColor: RequestBody,
        petbreed: RequestBody,
        discription: RequestBody,
        petImages: List<MultipartBody.Part>
    ): Flow<AddPetResponse> =
        repositry.AddPet(petname, petType, petColor, petbreed, discription, petImages)


    fun gettype() = viewModelScope.launch {
        repositry.getPetType()
            .onStart {
                _petData.value = PetTypeState.Loading
            }.catch { e ->
                _petData.value = PetTypeState.Failure(e)
            }.collect { response ->
                _petData.value = PetTypeState.Success(response)
            }
    }

    fun deletepetimages(imagename: String,id:String) = viewModelScope.launch {
        repositry.deletepetimage(imagename,id).onStart {
            _deletePetimage.value = Resource.Loading
        }.catch { e ->
            _deletePetimage.value = Resource.Failure(e)
        }.collect { response ->
            _deletePetimage.value = Resource.Success(response)
        }
    }

    fun editpetdetail(
        petName: String,
        petType: String,
        petColor: String,
        petBreed: String,
        description: String,
        id: String
    ) = viewModelScope.launch {
        repositry.editpet(petName, petType, petColor, petBreed, description, id).onStart {
            _editpetdata.value = Resource.Loading
        }.catch { e ->
            _editpetdata.value = Resource.Failure(e)
        }.collect { data ->
            _editpetdata.value = Resource.Success(data)
        }
    }

    fun editpetsphotos(
        id: RequestBody,
        petImages:   MultipartBody.Part
    ) = viewModelScope.launch {
        repositry.editpetsphotos(id,petImages).onStart {
            _editpetimage.value = Resource.Loading
        }.catch { e ->
            _editpetimage.value = Resource.Failure(e)
        }.collect { data ->
            _editpetimage.value = Resource.Success(data)
        }
    }


    fun petProfile(
        petId: String,
        profileImage: String
    ) = viewModelScope.launch {
        repositry.petProfile(petId, profileImage).onStart {
            _petprofileimage.value = Resource.Loading
        }.catch { e ->
            _petprofileimage.value = Resource.Failure(e)
        }.collect { data ->
            _petprofileimage.value = Resource.Success(data)
        }
    }



}