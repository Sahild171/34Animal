package com.application.animalAlertApp.View.Chats

import androidx.lifecycle.ViewModel
import com.application.animalAlertApp.data.Repositry.QuestionsRepositry
import com.application.animalAlertApp.data.Response.Questions.QusestionRespose
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class QuestionsViewModel @Inject constructor(val repositry: QuestionsRepositry):ViewModel(){


    fun addquestions(
        alertid: RequestBody,
        wherePetLastSeen: RequestBody,
        whenPetLastSeen: RequestBody,
        friendlyToApproach: RequestBody,
        contactDetail: RequestBody,
        otherComment: RequestBody,
        petImg: List<MultipartBody.Part>
    ): Flow<QusestionRespose> = repositry.addquestions(alertid,
        wherePetLastSeen,
        whenPetLastSeen,
        friendlyToApproach,
        contactDetail,
        otherComment,
        petImg)


}