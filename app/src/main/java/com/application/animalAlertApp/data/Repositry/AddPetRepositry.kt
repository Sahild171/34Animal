package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AddPetRepositry @Inject
constructor(private val apiService: ApiService) {


    fun AddPet(
        petname: RequestBody,
        petType: RequestBody,
        petColor: RequestBody,
        petbreed: RequestBody,
        discription: RequestBody,
        petImages: List<MultipartBody.Part>
    ): Flow<AddPetResponse> = flow {
        emit(apiService.addPet(petname, petType, petColor, petbreed, discription, petImages))
    }.flowOn(Dispatchers.IO)


    fun getPetType(
    ): Flow<PetTypeResponse> = flow {
        emit(apiService.getPetType())
    }.flowOn(Dispatchers.IO)

    fun deletepetimage(imagename: String, id: String): Flow<GenricResponse> = flow {
        emit(apiService.deletepetimage(imagename, id))
    }.flowOn(Dispatchers.IO)


    fun editpet(
        petName: String,
        petType: String,
        petColor: String,
        petBreed: String,
        description: String,
        id: String
    ): Flow<GenricResponse> = flow {
        emit(apiService.editpet(petName, petType, petColor, petBreed, description, id))
    }.flowOn(Dispatchers.IO)



    fun editpetsphotos(
        id: RequestBody,
        petImages:   MultipartBody.Part
    ): Flow<EditPetImages> = flow {
        emit(apiService.editpetsphotos(petImages,id))
    }.flowOn(Dispatchers.IO)


    fun petProfile(
        petId: String,
        profileImage: String
    ): Flow<GenricResponse> = flow {
        emit(apiService.petProfile(petId, profileImage))
    }.flowOn(Dispatchers.IO)



}