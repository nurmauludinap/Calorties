package com.una.calorties

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.una.calorties.databinding.ActivitySplahSreenBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SplahSreenActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var binding: ActivitySplahSreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplahSreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.titleCalorties.alpha = 0f
        binding.titleCalortiesTwo.alpha = 0f
        binding.titleCalortiesTwo.animate().setDuration(1500).alpha(1f)
        binding.titleCalorties.animate().setDuration(1500).alpha(1f)
            .withEndAction {
                val sharedPreference =
                    getSharedPreferences("CALORTIES", Context.MODE_PRIVATE)
                val token = sharedPreference.getString("token", null)
                if (token.isNullOrEmpty()) {
                    val i = Intent(this, MainActivity::class.java)

                    startActivity(i)
                } else {
                    checkIfUserAuthenticated()
                }
                overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                finish()
            }
    }

    private fun checkIfUserAuthenticated() {
        val sharedPreference =
            getSharedPreferences("CALORTIES", Context.MODE_PRIVATE)
        val token = sharedPreference.getString("token", null) ?: ""

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/me")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }

                if (response.code == 200 && !json?.getString("nama")
                        .isNullOrEmpty()
                ) {
                    val i = Intent(
                        this@SplahSreenActivity,
                        HomeActivity::class.java
                    )

                    startActivity(i)
                } else {
                    val editor = sharedPreference.edit()
                    editor.clear()
                    editor.apply()
                    val i = Intent(
                        this@SplahSreenActivity,
                        MainActivity::class.java
                    )

                    startActivity(i)
                }
            }
        })
    }
}