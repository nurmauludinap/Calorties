package com.una.calorties

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import com.una.calorties.fragments.LoadingDialog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ChangeProfileActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val loading = LoadingDialog(this)

    private lateinit var etEmail: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var etBirth: EditText
    private lateinit var etGender: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val backButton: ImageButton = findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val saveButton: Button = findViewById(R.id.saveBtn)
        saveButton.setOnClickListener {
            validate()
        }

        etEmail = findViewById(R.id.et_email)
        etWeight = findViewById(R.id.et_weight)
        etHeight = findViewById(R.id.et_height)
        etBirth = findViewById(R.id.et_birth)
        etGender = findViewById(R.id.et_gender)

        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        val sharedPreference = this.getSharedPreferences("CALORTIES",
            Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token", null) ?: ""

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
                    val email = json?.getString("email")
                    val weight = json?.getString("berat_badan")
                    val height = json?.getString("tinggi_badan")
                    val birth = json?.getString("birthdate")
                    val gender = json?.getString("gender")
                    Handler(Looper.getMainLooper()).post(Runnable {

                        etEmail.setText(email)
                        etWeight.setText(weight)
                        etHeight.setText(height)
                        etBirth.setText(birth)

                        // Create an ArrayAdapter using a list of values
                        val values = listOf("Male", "Female")
                        val adapter = ArrayAdapter(this@ChangeProfileActivity, android.R.layout.simple_spinner_item, values)

                        // Set the dropdown layout style
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        // Set the adapter to the spinner
                        etGender.adapter = adapter

                        // Set the desired value as selected in the spinner
                        val position = adapter.getPosition(gender)
                        etGender.setSelection(position)
                    })
                }
            }
        })
    }

    private fun validate() {
        val email = etEmail.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val height = etHeight.text.toString().trim()
        val birth = etBirth.text.toString().trim()
        val selectedGender = etGender.selectedItem.toString()
        val emailMatches = !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()

        if (email.isEmpty()) {
            etEmail.error = "Email required"
        } else if (emailMatches) {
            etEmail.error = "Email must be valid"
        }
        if (weight.isEmpty()) {
            etWeight.error = "Weight required"
        }
        if (height.isEmpty()) {
            etHeight.error = "Height required"
        }
        if (birth.isEmpty()) {
            etBirth.error = "Birth Date required"
        }
        if (email.isNotEmpty() && !emailMatches  && weight.isNotEmpty() && height.isNotEmpty()
            && birth.isNotEmpty()
        ) {
            changeProfile()
        }
    }

    private fun changeProfile() {
        loading.startLoading()
        val sharedPreference =
            getSharedPreferences("CALORTIES", Context.MODE_PRIVATE)
        val token = sharedPreference.getString("token", null) ?: ""
        val selectedGender = etGender.selectedItem.toString()

        val rawJson = JSONObject()
        rawJson.put("birthdate", etBirth.text.toString())
            .put("gender", selectedGender)
            .put("tinggi_badan", etHeight.text.toString())
            .put("berat_badan", etWeight.text.toString())
            .put("email", etEmail.text.toString())

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.create(JSON, rawJson.toString())

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/users")
            .addHeader("Authorization", "Bearer $token")
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }

                loading.isDismiss()
                if (response.code == 200) {
                    finish()
                } else {
                    val msg = json?.getString("detail")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@ChangeProfileActivity,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}