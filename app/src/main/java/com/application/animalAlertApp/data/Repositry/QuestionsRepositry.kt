package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.Questions.QusestionRespose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class QuestionsRepositry @Inject constructor(val apiService: ApiService) {


    fun addquestions(
        alertid: RequestBody,
        wherePetLastSeen: RequestBody,
        whenPetLastSeen: RequestBody,
        friendlyToApproach: RequestBody,
        contactDetail: RequestBody,
        otherComment: RequestBody,
        petImg: List<MultipartBody.Part>
    ): Flow<QusestionRespose> = flow {
        emit(
            apiService.addalertQuestion(
                alertid,
                wherePetLastSeen,
                whenPetLastSeen,
                friendlyToApproach,
                contactDetail,
                otherComment,
                petImg
            )
        )
    }.flowOn(Dispatchers.IO)

}