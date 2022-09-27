package com.application.animalAlertApp.View.AlertDetail

import com.application.animalAlertApp.data.Response.AlertDetailResponse

sealed class AlertDetailState {
    class Success(val data: AlertDetailResponse) : AlertDetailState()
    class Failure(val msg:Throwable) : AlertDetailState()
    object Loading  : AlertDetailState()
    object Empty : AlertDetailState()
}
