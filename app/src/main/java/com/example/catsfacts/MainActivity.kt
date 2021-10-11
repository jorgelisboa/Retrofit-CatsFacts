package com.example.catsfacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.catsfacts.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

const val BASE_URL = "https://cat-fact.herokuapp.com"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentData() // Get data from API
        binding.generateNewFact.setOnClickListener{
            getCurrentData()
        }
    }

    private fun getCurrentData() {
        binding.tvCatFact.visibility = View.INVISIBLE
        binding.tvDate.visibility = View.INVISIBLE
        binding.progress.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCatFacts().awaitResponse()
                if(response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.text)

                    withContext(Dispatchers.Main) {
                        binding.tvCatFact.visibility = View.VISIBLE
                        binding.tvDate.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE

                        binding.tvCatFact.text= data.text
                        binding.tvDate.text = data.createdAt
                    }
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvCatFact.text = "Verify your connection."
                }
            }
        }
    }


}