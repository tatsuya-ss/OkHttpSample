package com.example.okhttpsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.okhttpsample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

data class GitHub(val avatar_url: String)

interface GitHubService {
    @GET("users/tatsuya-ss")
    fun convertFromJson(): Call<GitHub>
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupButton()
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupButton() {
        binding.button.setOnClickListener {
            GlobalScope.launch {
                println("開始")
                withContext(Dispatchers.IO) {
                    fetchGitHub()
                }
                println("終了")
            }
        }
    }


    private fun makeOkHtttp(): OkHttpClient.Builder {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    .get()
                    .build()
            var response = chain.proceed(request)
            return@Interceptor response
        })
                .readTimeout(30, TimeUnit.SECONDS)

        return httpClient
    }

    private fun createService(): GitHubService {
        val client = makeOkHtttp().build()
        var retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        var api = retrofit.create(GitHubService::class.java)
        return api
    }

    private fun fetchGitHub() {
        createService().convertFromJson().enqueue(object : Callback<GitHub> {
            override fun onFailure(call: Call<GitHub>, t: Throwable) {
                println("失敗だ")
            }
            override fun onResponse(call: Call<GitHub>, response: Response<GitHub>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                            println(it.avatar_url)
                    }
                }
            }
        })
    }

}