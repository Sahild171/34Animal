package com.application.animalAlertApp.Model

data class Chat(
    var type:String?="",
    var msg: String? = "",
    var sendername: String? = "",
    var recivername: String? = "",
    var senderid: String? = "",
    var reciverid: String? = "",
    var timestamp: String="",
    var image:String="",
    var petimage:String="",
    var questionstext:String="",
    var mydevicetoken:String="",
    var Reciverdevicetoken:String=""

)

