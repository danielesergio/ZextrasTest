package com.danielesergio.zextrastest.client

import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.datasource.DataSource
import com.danielesergio.zextrastest.model.post.Post
import com.danielesergio.zextrastest.model.post.PostImp
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File


interface PostService: DataSource {
    @GET(DESC_POSTS_PATH)
    override suspend fun getPosts(@Query("_page") page: Int?,
                                  @Query("_limit") responseSize: Int?,
                                  @Query("_from") after: Long?): List<PostImp>

    @POST(POSTS_PATH)
    override suspend fun createPost(@Body newPost: Post): PostImp

    companion object{
        private const val POSTS_PATH:String = "/posts"
        
        private const val DESC_POSTS_PATH:String = "$POSTS_PATH?_order=desc"

        private const val BASE_URL:String = "https://jsonplaceholder.typicode.com/"

        private const val CACHE_SIZE = (10 * 1024 * 1024).toLong()

        private var instance: PostService? = null

        private val TAG = PostService::class.java.simpleName

        //cacheDir is used only for the first initialization
        fun getInstance(cacheDir: File):PostService{
            if(instance == null){
                val cache = Cache(File(cacheDir, "http_cache"), CACHE_SIZE)

                val client: OkHttpClient = OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(CacheInterceptor())
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                    .client(client)
                    .build()


                instance = retrofit.create(PostService::class.java)

                LoggerImpl.d(TAG, "Create new PostService instance with cache ${cacheDir.absolutePath}")
            }
            LoggerImpl.i(TAG, "Get PostService instance")
            return instance!!
        }

    }
}