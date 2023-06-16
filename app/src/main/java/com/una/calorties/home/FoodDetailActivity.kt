package com.una.calorties.home

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.navArgs
import com.una.calorties.R
import com.una.calorties.fragments.LoadingDialog
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream


class FoodDetailActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val args: FoodDetailActivityArgs by navArgs()
    private val loading = LoadingDialog(this)

    private lateinit var photo: Uri

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)
//
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val backButton: ImageButton = findViewById(R.id.back_btn)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val imageView = findViewById<ImageView>(R.id.foodPhoto)
        photo = Uri.parse(args.photo)
        imageView.setImageURI(photo)

        uploadPhoto(photo)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadPhoto(photo: Uri) {
        loading.startLoading()
        val sharedPreference = this.getSharedPreferences(
            "CALORTIES",
            Context.MODE_PRIVATE
        )
        val token = sharedPreference?.getString("token", null) ?: ""

        val inputStream = contentResolver.openInputStream(photo)
        val file = File(cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val mediaType = "image/jpeg".toMediaTypeOrNull()

        Log.i("photo name", file.name)
        photo.path?.let { Log.i("photo path", it) }

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, file.asRequestBody(mediaType))
            .build()

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/calories")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("accept", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.isDismiss()
                val resStr = e.message
                if (resStr != null) {
                    Log.i("photo fail", resStr)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body?.string()
                if (resStr != null) {
                    Log.i("photo", resStr)
                }

                if (response.code == 200) {
                    getTodayCalorie()
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodayCalorie() {
        val sharedPreference = this.getSharedPreferences(
            "CALORTIES",
            Context.MODE_PRIVATE
        )
        val token = sharedPreference?.getString("token", null) ?: ""
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val current = LocalDateTime.now().format(formatter)

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/calories/summary-day?date=$current")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.isDismiss()
            }

            override fun onResponse(call: Call, response: Response) {
                loading.isDismiss()
                val resStr = response.body?.string()

                if (resStr != null) {
                    Log.i("Today Calorie", resStr)
                }

                val json = resStr?.let { JSONObject(it) }
                val masukRaw = json?.getString("total_kalori_masuk")
                val kurangRaw = json?.getString("total_kalori_kurang")
                val berlebihRaw = json?.getString("total_kalori_berlebih")
                val harusRaw = json?.getString("target_kalori")

                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                val masuk = df.format(masukRaw?.toDouble()).toDouble()
                val kurang = df.format(kurangRaw?.toDouble()).toDouble()
                val berlebih = df.format(berlebihRaw?.toDouble()).toDouble()
                val harus = df.format(harusRaw?.toDouble()).toDouble()

                if (response.code == 200) {
                    val masukView = findViewById<TextView>(R.id.masukTxt)
                    val kurangView = findViewById<TextView>(R.id.kurangTxt)
                    val lebihView = findViewById<TextView>(R.id.lebihTxt)
                    val harusView = findViewById<TextView>(R.id.seharusnyaTxt)
                    Handler(Looper.getMainLooper()).post(Runnable {
                        masukView.text =
                            "Kalori hari ini : $masuk kkal"
                        kurangView.text =
                            "Kekurangan kalori hari ini : $kurang kkal"
                        lebihView.text =
                            "Kelebihan kalori hari ini : $berlebih kkal"
                        harusView.text =
                            "Total kalori seharusnya: $harus kkal"
                    })
                }
            }
        })
    }
}