package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GetMyPets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PetDetailRepositry @Inject constructor(val apiService: ApiService) {


    fun getPet(
        petid: String,
    ): Flow<GetMyPets> = flow {
        emit(apiService.getPetDetail(petid))
    }.flowOn(Dispatchers.IO)

}