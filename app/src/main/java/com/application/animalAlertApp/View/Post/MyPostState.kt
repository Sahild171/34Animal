package com.application.animalAlertApp.View.Post


import com.application.animalAlertApp.data.Response.Post.GetMyPost

sealed class MyPostState {
    class Success(val data: GetMyPost) : MyPostState()
    class Failure(val msg:Throwable) : MyPostState()
    object Loading  : MyPostState()
    object Empty : MyPostState()
}

