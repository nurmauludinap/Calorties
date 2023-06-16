package com.una.calorties.home

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
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
import java.text.SimpleDateFormat
import java.util.*


class CalorieDetailActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val args: CalorieDetailActivityArgs by navArgs()
    private val loading = LoadingDialog(this)

    private lateinit var textDate: TextView

    private var date = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        date = args.date
        textDate = findViewById(R.id.card_date)

        val formatIncoming = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        val formatOutgoing = SimpleDateFormat("EEEE, dd MMMM yyyy")
        val tz = TimeZone.getTimeZone("Asia/Jakarta")

        formatOutgoing.timeZone = tz
        textDate.text =
            formatIncoming.parse(date)?.let { formatOutgoing.format(it) }

        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
        loading.startLoading()
        val sharedPreference = this.getSharedPreferences(
            "CALORTIES",
            Context.MODE_PRIVATE
        )
        val token = sharedPreference?.getString("token", null) ?: ""

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/foods/daily?date=$date")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.isDismiss()
            }

            override fun onResponse(call: Call, response: Response) {
                loading.isDismiss()
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }

                if (response.code == 200) {
                    populateData(json ?: JSONObject())
                }
            }
        })
    }

    private fun populateData(data: JSONObject) {
        Handler(Looper.getMainLooper()).post(Runnable {
            val foodDetails = data.getJSONArray("food_details")
            val typeDetails = data.getJSONObject("total_by_type")

            val detailLayout = findViewById<LinearLayout>(R.id.detailCalorie)
            val totalLayout = findViewById<LinearLayout>(R.id.totallCalorie)

            var totalCalorie = 0.0

            if (foodDetails.length() < 1) {
                addText(detailLayout, "No data available")
                addText(totalLayout, "No data available")
            }

            for (i in 0 until foodDetails.length()) {
                val item = foodDetails.getJSONObject(i)
                addText(
                    detailLayout,
                    item.getString("name") + ": " + item.getString("jumlah_kalori") + " kkal"
                )
            }
            for (i in typeDetails.keys()) {
                val item = typeDetails.getString(i)
                val total = item.toFloat()
                totalCalorie += total
                addText(
                    totalLayout,
                    "$i: $item kkal"
                )
            }

            val calorieTxt = findViewById<TextView>(R.id.card_content)
            calorieTxt.text = totalCalorie.toString() + " kkal"
        })
    }

    private fun addText(layout: LinearLayout, text: CharSequence) {
        val tvContent = TextView(this)
        if (tvContent.parent != null) {
            (tvContent.parent as ViewGroup).removeView(tvContent)
        }

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(40, 0, 0, 0)

        tvContent.textSize = 12f
        tvContent.layoutParams = params
        tvContent.setTypeface(tvContent.typeface, Typeface.BOLD)
        tvContent.text = text

        layout.addView(tvContent)
    }

}