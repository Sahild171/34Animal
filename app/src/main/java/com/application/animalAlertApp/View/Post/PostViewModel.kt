package com.application.animalAlertApp.View.Post

import androidx.lifecycle.ViewModel
import com.application.animalAlertApp.data.Repositry.AddPostRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(val repositry: AddPostRepositry):ViewModel(){


    fun addpost(
        description:RequestBody,
        location:RequestBody,
        postImg:List<MultipartBody.Part>
    ) = repositry.addPost(description, location, postImg)



}