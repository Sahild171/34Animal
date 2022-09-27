package com.application.animalAlertApp.View.Payments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.PaymentRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Payment.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(val repositry: PaymentRepositry) : ViewModel() {

    private val _paymentdata: MutableLiveData<Resource<PaymentResponse>> = MutableLiveData()
    val paymentdata: LiveData<Resource<PaymentResponse>> = _paymentdata

    private val _paymentdata6mon: MutableLiveData<Resource<PaymentResponse>> = MutableLiveData()
    val paymentdata6mon: LiveData<Resource<PaymentResponse>> = _paymentdata6mon

    private val _cancelsubs: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val cancelsubs: LiveData<Resource<GenricResponse>> = _cancelsubs

    private val _updatecarddata: MutableLiveData<Resource<UpdateCardResponse>> =
        MutableLiveData()
    val updatecarddata: LiveData<Resource<UpdateCardResponse>> = _updatecarddata

    private val _subscriptionstatus: MutableLiveData<Resource<SubscriptionStatus>> =
        MutableLiveData()
    val subscriptionstatus: LiveData<Resource<SubscriptionStatus>> = _subscriptionstatus


    fun makepayment(number: String, exp_month: String, exp_year: String, cvc: String) =
        viewModelScope.launch {
            repositry.payment(number, exp_month, exp_year, cvc).onStart {
                _paymentdata.value = Resource.Loading
            }.catch { e ->
                _paymentdata.value = Resource.Failure(e)
            }.collect { response ->
                _paymentdata.value = Resource.Success(response)
            }
        }


    fun makepayment6month(number: String, exp_month: String, exp_year: String, cvc: String) =
        viewModelScope.launch {
            repositry.payment6month(number, exp_month, exp_year, cvc).onStart {
                _paymentdata6mon.value = Resource.Loading
            }.catch { e ->
                _paymentdata6mon.value = Resource.Failure(e)
            }.collect { response ->
                _paymentdata6mon.value = Resource.Success(response)
            }
        }


            fun updatecard(
                cardId: String,
                number: String,
                exp_month: String,
                exp_year: String,
                cvc: String,
                cardholdername: String
            ) =
                viewModelScope.launch {
                    repositry.updateCard(cardId, number, exp_month, exp_year, cvc, cardholdername)
                        .onStart {
                            _updatecarddata.value = Resource.Loading
                        }.catch { e ->
                        _updatecarddata.value = Resource.Failure(e)
                    }.collect { response ->
                        _updatecarddata.value = Resource.Success(response)
                    }
                }


            fun cancelsubs() =
                viewModelScope.launch {
                    repositry.cancelsubscription().onStart {
                        _cancelsubs.value = Resource.Loading
                    }.catch { e ->
                        _cancelsubs.value = Resource.Failure(e)
                    }.collect { response ->
                        _cancelsubs.value = Resource.Success(response)
                    }
                }


            fun getsubscriptionstatus() =
                viewModelScope.launch {
                    repositry.getsubscriptionStatus().onStart {
                        _subscriptionstatus.value = Resource.Loading
                    }.catch { e ->
                        _subscriptionstatus.value = Resource.Failure(e)
                    }.collect { response ->
                        _subscriptionstatus.value = Resource.Success(response)
                    }
                }


}