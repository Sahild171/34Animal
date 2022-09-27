package com.application.animalAlertApp.Interfaces

interface RequestInterface {
    fun onAccept(pos:Int,id:String,senderId:String)
    fun onDecline(pos: Int,id: String)
}