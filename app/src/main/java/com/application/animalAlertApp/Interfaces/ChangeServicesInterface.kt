package com.application.animalAlertApp.Interfaces

import com.application.animalAlertApp.data.Response.BusinessProfile.Service

interface ChangeServicesInterface {
    fun onedit(pos: Int, service: Service)
    fun ondelete(pos: Int, serviceid: String)
}