package com.danielesergio.zextrastest.client

import com.danielesergio.zextrastest.model.datasource.DataSource
import com.danielesergio.zextrastest.model.post.Post
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface PostService: DataSource {
    @GET(POSTS_PAT)
    override suspend fun getPosts(@Query("_page")page: Int?): List<Post>

    @POST(POSTS_PAT)
    override suspend fun createPost(@Body newPost: Post): Post

    companion object{
        private const val POSTS_PAT:String = "/posts"
        private const val BASE_URL:String = "https://jsonplaceholder.typicode.com/"

        private var instance: PostService? = null

        fun getInstance():PostService{
            if(instance == null){

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build()

                instance = retrofit.create(PostService::class.java)
            }

            return instance!!
        }

    }
}