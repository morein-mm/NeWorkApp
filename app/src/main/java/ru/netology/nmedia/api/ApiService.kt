package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.dto.User

interface ApiService {

    // Posts API

    // Получение данных
    @GET("posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getPostsNewerThan(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}/before")
    suspend fun getPostsBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<Post>

    // Изменение данных
    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    // Лайки
    @POST("posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikePost(@Path("id") id: Long): Response<Post>

    // Events API

    // Получение данных
    @GET("events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}/newer")
    suspend fun getEventsNewerThan(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getEventsBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>

    // Изменение данных
    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<Unit>

    // Лайки
    @POST("events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeEvent(@Path("id") id: Long): Response<Event>

    // Users API

    // Получение данных о пользователях
    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    // Регистрация и обновление пользователей
    @Multipart
    @POST("users/registration")
    suspend fun registerUser(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part? = null // Аватар передается опционально
    ): Response<Token>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authenticateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    // Работа с токенами
    @POST("users/push-tokens")
    suspend fun savePushToken(@Body token: PushToken): Response<Unit>

    // Jobs
    @GET("{userId}/jobs")
    suspend fun getJobsByUserId(@Path("userId") userId: Long): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{jobId}")
    suspend fun deleteJobById(@Path("jobId") jobId: Long): Response<Unit>

    // Media
    @Multipart
    @POST("media")
    suspend fun upload(@Part file: MultipartBody.Part): Response<Media>

}