package com.application.animalAlertApp.Network

import com.application.animalAlertApp.data.Response.*
import com.application.animalAlertApp.data.Response.Auth.LoginData
import com.application.animalAlertApp.data.Response.Auth.SocialLogin
import com.application.animalAlertApp.data.Response.BusinessProfile.*
import com.application.animalAlertApp.data.Response.Comments.AddComment
import com.application.animalAlertApp.data.Response.Comments.CommentReplyresponse
import com.application.animalAlertApp.data.Response.Comments.GetCommentsResponse
import com.application.animalAlertApp.data.Response.Comments.GetReplies
import com.application.animalAlertApp.data.Response.OtherUserProfile.GetUserProfile
import com.application.animalAlertApp.data.Response.Payment.*
import com.application.animalAlertApp.data.Response.Post.GetAllPosts
import com.application.animalAlertApp.data.Response.Post.GetMyPost
import com.application.animalAlertApp.data.Response.Post.PostLikeResponse
import com.application.animalAlertApp.data.Response.Profile.EditProfileData
import com.application.animalAlertApp.data.Response.Profile.EditProfilePic
import com.application.animalAlertApp.data.Response.Profile.SearchUserResponse
import com.application.animalAlertApp.data.Response.Questions.QusestionRespose
import com.application.animalAlertApp.data.Response.Request.GetfriendRequests
import com.application.animalAlertApp.data.Response.Service.ServiceResponse
import com.application.animalAlertApp.data.Response.Shop.AddShopResponse
import com.application.animalAlertApp.data.Response.Shop.GetAllShops
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    companion object {
        const val BASE_URL = "https://animalalertcommunity.org/nodeapis/"
        const val IMAGE_BASE_URL = "https://animalalertcommunity.org/uploads/profileImg/"
        const val PET_IMAGE_BASE_URL = "https://animalalertcommunity.org/uploads/PetsImg/"
        const val POST_IMAGE_BASE_URL = "https://animalalertcommunity.org/uploads/postImg/"
        const val SHOP_IMAGE_BASE_URL = "https://animalalertcommunity.org/uploads/shopsImg/"
    }

    //////////////////Login///////////////////
    @FormUrlEncoded
    @POST("User/login")
    suspend fun login(
        @Field("phoneNo") phoneNo: String,
        @Field("password") password: String,
        @Field("deviceToken") deviceToken: String
    ): LoginData


    //////////////////Register/////////////////
    @FormUrlEncoded
    @POST("User/registerUser")
    suspend fun registerUser(
        @Field("phoneNo") phoneNo: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("location") location: String,
        @Field("password") password: String,
        @Field("dialCode") dialCode: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("deviceToken") deviceToken: String
    ): RegisterResponse


    //////////////////SocialLogin/////////////////
    @FormUrlEncoded
    @POST("User/googleUser")
    suspend fun socailogin(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("deviceToken") deviceToken: String
    ): SocialLogin


    //////////////////VerifyOtp/////////////////
    @FormUrlEncoded
    @POST("User/verifyOtp")
    suspend fun verifyOtp(
        @Field("id") id: String,
        @Field("otp") otp: String
    ): OtpResponse


    ////////////////ResendOtp///////////////////
    @FormUrlEncoded
    @POST("User/resendOtp")
    suspend fun resendotp(
        @Field("id") id: String
    ): ResendOtp


    /////////////////ChangePhoenNo///////////////
    @FormUrlEncoded
    @POST("User/changePhoneNo")
    suspend fun changePhoneno(
        @Field("user_id") user_id: String,
        @Field("phoneNo") phoneNo: String
    ): ChangePhoneResponse


    ///////////////SavePrefrences//////////////////
    @FormUrlEncoded
    @POST("User/savePerferences")
    suspend fun saveprefrence(
        @Field("user_id") user_id: String,
        @Field("perference") perference: String
    ): SavePrefrence


    /////////////////AddPet/////////////////
    @Multipart
    @POST("User/addingPet")
    suspend fun addPet(
        @Part("petName") petName: RequestBody,
        @Part("petType") petType: RequestBody,
        @Part("petColor") petColor: RequestBody,
        @Part("petBreed") petBreed: RequestBody,
        @Part("description") description: RequestBody,
        @Part petImages: List<MultipartBody.Part>
    ): AddPetResponse


    //////////////////GetAlerts////////////////
    @POST("User/getAlerts")
    suspend fun getAlert(): GetAllAlerts


    //////////////////AddAlert/////////////////
    @FormUrlEncoded
    @POST("User/alert")
    suspend fun addalert(
        @Field("addTitle") addTitle: String,
        @Field("priority") priority: String,
        @Field("description") description: String,
        @Field("Status") Status: String,
        @Field("petId") petId: String?
    ): AddAlertResponse

    //////////////TypesOfPet ByAdmin///////////
    @GET("User/getAdminPet")
    suspend fun getPetType(): PetTypeResponse


    ////////////////GetMyPets///////////////////////
    @GET("User/getPet")
    suspend fun getPets(): GetMyPets


    ////////////////////DeleteAlerts///////////////
    @FormUrlEncoded
    @POST("User/deleteAlert")
    suspend fun deleteAlert(@Field("alertId") alertId: String): DeleteAlertsResponse


    ////////////////////DeleteAlerts///////////////
    @FormUrlEncoded
    @PUT("User/closeAlert")
    suspend fun closeAlert(
        @Field("alertId") alertId: String,
        @Field("isActive") isActive: Boolean
    ): DeleteAlertsResponse


    ///////////////////EditAlerts/////////////////
    @FormUrlEncoded
    @PUT("User/editAlert")
    suspend fun editAlert(
        @Field("alertId") alertId: String,
        @Field("addTitle") addTitle: String,
        @Field("petId") petId: String?,
        @Field("priority") priority: String,
        @Field("description") description: String
    ): EditAlertResponse


    ///////////////////AlertDetail/////////////////
    @FormUrlEncoded
    @POST("User/getAlert")
    suspend fun getDetailAlert(
        @Field("_id") _id: String
    ): AlertDetailResponse


    ///////////////////HomePagePosts///////////////
    @FormUrlEncoded
    @POST("User/getPost")
    suspend fun getAllPost(@Field("page") page: Int): GetAllPosts


    /////////////////////AlertQuestions////////////
    @Multipart
    @POST("User/alertQuestion")
    suspend fun addalertQuestion(
        @Part("alertId") alertId: RequestBody,
        @Part("wherePetLastSeen") wherePetLastSeen: RequestBody,
        @Part("whenPetLastSeen") whenPetLastSeen: RequestBody,
        @Part("friendlyToApproach") friendlyToApproach: RequestBody,
        @Part("contactDetail") contactDetail: RequestBody,
        @Part("otherComment") otherComment: RequestBody,
        @Part petImg: List<MultipartBody.Part>
    ): QusestionRespose


    /////////////////////Report///////////////////
    @FormUrlEncoded
    @POST("User/reportAlert")
    suspend fun reportalert(
        @Field("alertId") alertId: String,
        @Field("reportBy") reportBy: String,
        @Field("reason") reason: String
    ): GenricResponse


    //////////////////////AddPost///////////////////
    @Multipart
    @POST("User/addPost")
    suspend fun addpost(
        @Part("description") description: RequestBody,
        @Part("location") location: RequestBody,
        @Part postImg: List<MultipartBody.Part>
    ): GenricResponse

    //////////////////////MyPosts///////////////////
    @POST("User/getUserPost")
    suspend fun getmypost(): GetMyPost


    //////////////////////EditProfile///////////////
    @FormUrlEncoded
    @PUT("User/editProfile")
    suspend fun editprofile(
        @Field("name") name: String,
        @Field("phoneNo") phoneNo: String,
        @Field("location") location: String,
        @Field("email") email: String,
        @Field("lat") lat:String,
        @Field("long") long:String): EditProfileData


    //////////////////////EditProfilePic//////////////
    @Multipart
    @PUT("User/editProfilePic")
    suspend fun editprofilepic(
        @Part image: MultipartBody.Part
    ): EditProfilePic


    /////////////////////GetQuestionStatus////////////
    @FormUrlEncoded
    @POST("User/getScreen")
    suspend fun checkQuestionstatus(
        @Field("alertId") alertId: String
    ): GenricResponse


    //////////////////////PetDetail//////////////////
    @FormUrlEncoded
    @POST("User/getPetDetail")
    suspend fun getPetDetail(@Field("petId") petId: String): GetMyPets


    ///////////////////////GetUserProfile/////////////
    @FormUrlEncoded
    @POST("User/getProfile")
    suspend fun getuserprofile(@Field("recieverId") userId: String): GetUserProfile


    //////////////////////SearchUser////////////
    @FormUrlEncoded
    @POST("User/searchUser")
    suspend fun searchUser(@Field("name") name: String): SearchUserResponse


    /////////////////////SendRequest/////////////////
    @FormUrlEncoded
    @POST("User/sentRequest")
    suspend fun sentRequest(
        @Field("recieverId") recieverId: String,
        @Field("deviceToken") deviceToken: String
    ): GenricResponse


    ////////////////////////GetRequest/////////////////
    @FormUrlEncoded
    @POST("User/friendRequest")
    suspend fun getrequest(@Field("status") status: String): GetfriendRequests


    //////////////////AcceptRejectRequest////////
    @FormUrlEncoded
    @PUT("User/acceptReject")
    suspend fun acceptReject(
        @Field("status") status: String,
        @Field("friendId") friendId: String,
        @Field("senderId") senderId: String
    ): GenricResponse


    ///////////////////////MyFriends/////////////
    @FormUrlEncoded
    @POST("User/friends")
    suspend fun friends(@Field("status") status: String): GetfriendRequests


    ////////////////////RemoveFriend/////////
    @FormUrlEncoded
    @POST("User/removeFriend")
    suspend fun removeFriend(@Field("friendId") friendId: String): GenricResponse

    ////////////////////GetPostComments/////////
    @FormUrlEncoded
    @POST("User/getComment")
    suspend fun getComment(@Field("postId") postId: String): GetCommentsResponse

    ////////////////////AddPostComments/////////
    @FormUrlEncoded
    @POST("User/comment")
    suspend fun addcomment(
        @Field("postId") postId: String,
        @Field("comment") comment: String
    ): AddComment

    /////////////////////getReplies////////////////
    @FormUrlEncoded
    @POST("User/getReplies")
    suspend fun getReplies(@Field("commentId") commentId: String): GetReplies

    /////////////////////AddRepltonComment//////////
    @FormUrlEncoded
    @POST("User/replyComment")
    suspend fun replyComment(
        @Field("commentId") commentId: String,
        @Field("reply") reply: String
    ): CommentReplyresponse


    ///////////////////////LikeComment////////////
    @FormUrlEncoded
    @POST("User/CommentLike")
    suspend fun likecomment(@Field("commentId") commentId: String): GenricResponse


    //////////////////////AddShop//////////////
    @FormUrlEncoded
    @POST("User/addShop")
    suspend fun addShop(
        @Field("businessName") businessName: String,
        @Field("businessDescription") businessDescription: String,
        @Field("mobileNo") mobileNo: String,
        @Field("location") location: String
    ): AddShopResponse


    ////////////////AddShopServvice//////////////
    @FormUrlEncoded
    @POST("User/addShopService")
    suspend fun addshopservice(@Field("services") services: String): GenricResponse

    ////////////////////AddPortfolio//////////////
    @Multipart
    @PUT("User/addShopPic")
    suspend fun addShopPic(
        @Part uploadLogo: MultipartBody.Part,
        @Part coverPhoto: MultipartBody.Part,
        @Part portfolioLogo: List<MultipartBody.Part>
    ): GenricResponse


    ////////////////GetAllShops//////////////////
    @GET("User/getShops")
    suspend fun getAllshops(): GetAllShops


    /////////////////GetNotifications///////////
    @GET("User/getNotification")
    suspend fun getNotification(): NotificationResponse


    ///////////////PostLike//////////////////
    @FormUrlEncoded
    @POST("User/like")
    suspend fun likepost(@Field("postId") postId: String): PostLikeResponse


    /////////////////GetServices//////////////
    @GET("User/getService")
    suspend fun getService(): ServiceResponse


    ///////////////GetBusinessProfile///////////
    @FormUrlEncoded
    @POST("User/getShop")
    suspend fun getShop(@Field("userId") userId: String): BusinessProfileResponse


    ////////////////EditCoverPhoto//////////////
    @Multipart
    @PUT("User/editShopDetail")
    suspend fun editcoverphoto(@Part coverPhoto: MultipartBody.Part): EditcoverResopnse


    ////////////////EditShopLogo//////////////
    @Multipart
    @PUT("User/editShopDetail")
    suspend fun editshoplogo(@Part uploadLogo: MultipartBody.Part): GenricResponse


    ///////////////EditPortfolio////////////
    @Multipart
    @PUT("User/addPhoto")
    suspend fun editportfolio(@Part portfolioLogo: MultipartBody.Part): AddPortfolioresponse


    /////////////DeleteService///////////
    @DELETE("User/deleteService/{serviceId}")
    suspend fun deleteService(@Path("serviceId") serviceId: String): GenricResponse


    //////////////deletePotfolioPhoto////////////
    @FormUrlEncoded
    @PUT("User/deletePhoto")
    suspend fun deleteportfoliophoto(@Field("portfolioLogo") portfolioLogo: String): GenricResponse


    ///////////////////editService/////////////
    @FormUrlEncoded
    @PUT("User/editService")
    suspend fun editService(
        @Field("_id") _id: String,
        @Field("service") service: String,
        @Field("pricePeriod") pricePeriod: String,
        @Field("price") price: String,
        @Field("serviceDescription") serviceDescription: String
    ): EditServiceResponse


    ///////////////////editShopDetail/////////////
    @FormUrlEncoded
    @PUT("User/editShopDetail")
    suspend fun editShopDetail(
        @Field("businessName") businessName: String,
        @Field("businessDescription") businessDescription: String,
        @Field("mobileNo") mobileNo: String,
        @Field("location") location: String
    ): EditBusinessResponse


    ///////////////////ForgotPassword////////////
    @FormUrlEncoded
    @POST("User/forgotPassword")
    suspend fun forgotPassword(@Field("email") email: String): ForgotResponse


    ////////////////VerifyOtpForgot/////////////
    @FormUrlEncoded
    @POST("User/otpVerifyforPass")
    suspend fun otpVerifyforPass(
        @Field("userId") userId: String,
        @Field("otp") otp: String
    ): VerifyOtpResponse

    ////////////////ForgotResetPassword/////////////
    @FormUrlEncoded
    @PUT("User/updatePass")
    suspend fun updatePass(
        @Field("id") id: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String
    ): GenricResponse


    ///////////////////ChangePassword//////////
    @FormUrlEncoded
    @PUT("User/resetPassword")
    suspend fun resetPassword(
        @Field("oldpassword") oldpassword: String,
        @Field("password") password: String
    ): GenricResponse

    //////////////////ContactUs///////////////////
    @FormUrlEncoded
    @POST("User/contact")
    suspend fun contact(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("message") message: String
    ): GenricResponse

    /////////////////////Payments////////////////
    @FormUrlEncoded
    @POST("admin/payment")
    suspend fun payment(
        @Field("number") number: String,
        @Field("exp_month") exp_month: String,
        @Field("exp_year") exp_year: String,
        @Field("cvc") cvc: String
    ): PaymentResponse


    /////////////////CancelPayment/////////////
    @POST("admin/updatePaymentStatus")
    suspend fun updatePaymentStatus(): GenricResponse


    ////////////////////SaveCard////////////////////
    @FormUrlEncoded
    @POST("admin/saveCard")
    suspend fun savecard(
        @Field("number") number: String,
        @Field("exp_month") exp_month: String,
        @Field("exp_year") exp_year: String,
        @Field("cvc") cvc: String,
        @Field("cardHolderName") cardHolderName: String
    ): SavecardResponse

    ////////////////////////GetCard///////////////////
    @GET("admin/getCard")
    suspend fun getCard(): GetCardResponse

    /////////////////GetTransactioHistory//////////////
    @GET("admin/getTransaction")
    suspend fun gettransaction(): GetTransactionHistrory


    ////////////////CheckShopStatus//////////////////////
    @GET("User/checkStatus")
    suspend fun checkStatus(): ShopStatusResponse


    ////////////UpdateCard////////////////
    @FormUrlEncoded
    @PUT("admin/updateCard")
    suspend fun updateCard(
        @Field("cardId") cardId: String,
        @Field("number") number: String,
        @Field("exp_month") exp_month: String,
        @Field("exp_year") exp_year: String,
        @Field("cvc") cvc: String,
        @Field("cardHolderName") cardHolderName: String
    ): UpdateCardResponse


    ////////////////////DeleteCard/////////////
    @FormUrlEncoded
    @POST("admin/deleteCard")
    suspend fun deletecard(@Field("cardId") cardId: String): GenricResponse


    //////////////////Delete pet/////////////////
    @DELETE("User/deletePetPost/{id}")
    suspend fun deletepet(@Path("id") id: String): GenricResponse


    ////////////Delete Pet Images///////////
    @FormUrlEncoded
    @PUT("User/deletePetPhoto")
    suspend fun deletepetimage(
        @Field("petImages") imagename: String,
        @Field("id") id: String
    ): GenricResponse


    ///////////////Editpet///////////////
    @FormUrlEncoded
    @PUT("User/editPetData")
    suspend fun editpet(
        @Field("petName") petName: String,
        @Field("petType") petType: String,
        @Field("petColor") petColor: String,
        @Field("petBreed") petBreed: String,
        @Field("description") description: String,
        @Field("id") id: String
    ): GenricResponse

    //////////////EditPet Photos/////////////
    @Multipart
    @PUT("User/editPets")
    suspend fun editpetsphotos(
        @Part petImages: MultipartBody.Part,
        @Part("id") id: RequestBody
    ): EditPetImages


    ////////////////////////SubscriptionStatus/////////////////
    @POST("admin/getSubsData")
    suspend fun getsubscriptionStatus(): SubscriptionStatus


    /////////////////////////getScreen////////////////////
    @FormUrlEncoded
    @POST("User/getScreen")
    suspend fun getscreen(@Field("alertId") alertId: String): GenricResponse

//    //////////////DeleteComment///////////
//    @DELETE("User/deleteComment/{commentId}")
//    suspend fun deletecomment(@Path("commentId") commentId: String): GenricResponse


    ///////////////////////user/changePreference///////////////
    @FormUrlEncoded
    @PUT("user/changePreference")
    suspend fun changeprefrence(@Field("perference") perference: String): GenricResponse


    ///////////////////////payment6months//////////////
    @FormUrlEncoded
    @POST("admin/payment6months")
    suspend fun payment6months(
        @Field("number") number: String,
        @Field("exp_month") exp_month: String,
        @Field("exp_year") exp_year: String,
        @Field("cvc") cvc: String
    ): PaymentResponse


    ///////////////////////user/petProfile///////////////
    @FormUrlEncoded
    @POST("user/petProfile")
    suspend fun petProfile(
        @Field("petId") petId: String,
        @Field("profileImage") profileImage: String
    ): GenricResponse


    //////////////////////EditProfile///////////////
    @FormUrlEncoded
    @PUT("User/editProfile")
    suspend fun editlocationvisibility(
        @Field("locationVisibility") locationVisibility:Boolean): GenricResponse

}