package com.application.animalAlertApp.Interfaces

import com.application.animalAlertApp.data.Response.MessageX

interface AlertsOptionInterface {
    fun onEdit(pos:Int,alertId:String,title:String,petname:String,priority:String,discription:String,petid:String)
    fun OnClosePost(pos: Int,alertId: String)
    fun OnDelete(pos: Int,alertId: String)
    fun OnChat(pos: Int,alertId: String,list: MessageX)
    fun OnReport(pos: Int,alertId: String)
    fun OnDetail(alertId: String,name:String)
}