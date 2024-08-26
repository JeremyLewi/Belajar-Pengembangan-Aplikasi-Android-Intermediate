package com.example.storyapp.data.remote.retrofit


import com.example.storyapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String, @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryResponse

    @GET("stories")
    fun getAllWidgetStory(
        @Header("Authorization") token: String,
    ): Call<StoryResponse>

    @GET("stories")
    fun getAllStoryLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int,

        ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<NewStoryResponse>

}
