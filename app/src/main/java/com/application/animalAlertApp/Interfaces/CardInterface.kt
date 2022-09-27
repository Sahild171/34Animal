package com.application.animalAlertApp.Interfaces

import com.application.animalAlertApp.data.Response.Payment.FindCard

interface CardInterface {
    fun onCardEdit(card: FindCard,pos: Int)
    fun onDeleteCard(cardId:String,pos:Int)
    fun onSetasDefault(cardId:String)
}