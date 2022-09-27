package com.application.animalAlertApp.Interfaces

import com.application.animalAlertApp.data.Response.MessageXX

interface SeePetInterface {
    fun onseepet(pos:Int,data:MessageXX)
}