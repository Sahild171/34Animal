package com.application.animalAlertApp.data.Response

data class User(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val email: String,
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
    val location: String,
    val name: String,
    val otp: Int,
    val password: String,
    val perference: List<String>,
    val phoneNo: String,
    val updatedAt: String
)