package com.example.okhttpsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.okhttpsample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

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
                    fetch()
                }
                println("終了")
            }
        }
    }

    private fun fetch() {
        val client: OkHttpClient = OkHttpClient()
        val urlString: String = "https://api.github.com/users/tatsuya-ss"
        val body: FormBody = FormBody.Builder().build()
        // request = Request{method=POST, url=https://api.github.com/users/tatsuya-ss/}
        val request  = Request.Builder().url(urlString).get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("大失敗: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                // .toString()ではなく、string()
                // https://stackoverflow.com/questions/32598044/how-can-i-extract-the-raw-json-string-from-an-okhttp-response-object
                println("大成功: ${response.body?.string()}")
            }
        })
    }

}