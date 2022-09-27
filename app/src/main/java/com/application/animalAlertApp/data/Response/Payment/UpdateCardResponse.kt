package com.application.animalAlertApp.data.Response.Payment

import com.application.animalAlertApp.data.Response.DocsXXX

data class UpdateCardResponse(val updateCard: DocsXXX,
                              val message: String,
                              val status: Int)
