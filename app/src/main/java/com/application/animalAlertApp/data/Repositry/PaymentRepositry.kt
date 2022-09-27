package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Payment.*
import com.application.animalAlertApp.data.Response.SavecardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentRepositry @Inject constructor(val apiService: ApiService){

    fun payment(number:String,exp_month:String,exp_year:String,cvc:String): Flow<PaymentResponse> = flow {
        emit(apiService.payment(number, exp_month, exp_year, cvc))
    }.flowOn(Dispatchers.IO)

    fun payment6month(number:String,exp_month:String,exp_year:String,cvc:String): Flow<PaymentResponse> = flow {
        emit(apiService.payment6months(number, exp_month, exp_year, cvc))
    }.flowOn(Dispatchers.IO)

    fun savecard(number:String,exp_month:String,exp_year:String,cvc:String,cardholdername:String): Flow<SavecardResponse> = flow {
        emit(apiService.savecard(number, exp_month, exp_year, cvc,cardholdername))
    }.flowOn(Dispatchers.IO)

    fun getcard(): Flow<GetCardResponse> = flow {
        emit(apiService.getCard())
    }.flowOn(Dispatchers.IO)


    fun gettransaction(): Flow<GetTransactionHistrory> = flow {
        emit(apiService.gettransaction())
    }.flowOn(Dispatchers.IO)



    fun updateCard(cardId:String,number:String,exp_month:String,exp_year:String,cvc:String,cardholdername:String): Flow<UpdateCardResponse> = flow {
        emit(apiService.updateCard(cardId,number,exp_month,exp_year,cvc,cardholdername))
    }.flowOn(Dispatchers.IO)

    fun deletecard(cardId:String): Flow<GenricResponse> = flow {
        emit(apiService.deletecard(cardId))
    }.flowOn(Dispatchers.IO)


    fun cancelsubscription(): Flow<GenricResponse> = flow {
        emit(apiService.updatePaymentStatus())
    }.flowOn(Dispatchers.IO)


    fun getsubscriptionStatus(): Flow<SubscriptionStatus> = flow {
        emit(apiService.getsubscriptionStatus())
    }.flowOn(Dispatchers.IO)


}