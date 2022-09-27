package com.application.animalAlertApp.data.Response.Auth



data class FindUser(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val dialCode: String,
    val email: String,
    val image: String,
    val isActive: Boolean,
    val isApproved: Boolean,
    val isBlocked: Boolean,
    val isComment: Boolean,
    val isDeleted: Boolean,
    val isLike: Boolean,
    val isOtpVerify: Boolean,
    val isPerferenceSet: Boolean,
    val isPhoneVerified: Boolean,
    val isPostAccepted: Boolean,
    val isPostRejected: Boolean,
    val isProfileSetup: Boolean,
    val jwtToken: String,
    val location: String,
    val name: String,
    val online: Boolean,
    val otp: Int,
    val password: String,
    val perference: List<String>,
    val phoneNo: String,
    val updatedAt: String,
    val userType: String,
    val deviceToken:String
)