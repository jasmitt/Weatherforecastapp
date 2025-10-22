package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // TODO: Get your free API key from https://openweathermap.org/api
    private val API_KEY = "0c51ee9568209a229308965028b02a4a"

    private lateinit var etCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherInfoLayout: LinearLayout
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        btnGetWeather.setOnClickListener {
            val cityName = etCity.text.toString().trim()

            if (cityName.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fetchWeather(cityName)
        }
    }

    private fun initViews() {
        etCity = findViewById(R.id.etCity)
        btnGetWeather = findViewById(R.id.btnGetWeather)
        progressBar = findViewById(R.id.progressBar)
        weatherInfoLayout = findViewById(R.id.weatherInfoLayout)
        tvCityName = findViewById(R.id.tvCityName)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvDescription = findViewById(R.id.tvDescription)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWindSpeed = findViewById(R.id.tvWindSpeed)
        tvError = findViewById(R.id.tvError)
    }

    private fun fetchWeather(cityName: String) {
        progressBar.visibility = View.VISIBLE
        weatherInfoLayout.visibility = View.GONE
        tvError.visibility = View.GONE
        btnGetWeather.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.weatherApiService.getWeather(
                    cityName = cityName,
                    apiKey = API_KEY
                )

                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()!!
                    displayWeather(weatherData)
                } else {
                    showError("City not found or API error: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
                btnGetWeather.isEnabled = true
            }
        }
    }

    private fun displayWeather(weather: WeatherResponse) {
        weatherInfoLayout.visibility = View.VISIBLE
        tvError.visibility = View.GONE

        tvCityName.text = weather.cityName
        tvTemperature.text = "Temperature: ${weather.main.temperature}°C"
        tvDescription.text = "Description: ${weather.weather[0].description.replaceFirstChar { it.uppercase() }}"
        tvHumidity.text = "Humidity: ${weather.main.humidity}%"
        tvWindSpeed.text = "Wind Speed: ${weather.wind.speed} m/s"
    }

    private fun showError(message: String) {
        weatherInfoLayout.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }
}