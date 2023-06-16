package com.una.calorties

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.una.calorties.fragments.LoadingDialog
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity(),
    DatePickerDialog.OnDateSetListener {

    private val client = OkHttpClient()

    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var etBirth: EditText
    private lateinit var etGender: Spinner
    private val loading = LoadingDialog(this)

    var day = 0
    var month = 0
    var year = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginTxt = findViewById(R.id.login_txt) as TextView
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        etEmail = findViewById(R.id.et_email)
        etHeight = findViewById(R.id.et_height)
        etWeight = findViewById(R.id.et_weight)
        etBirth = findViewById(R.id.et_birth)
        etGender = findViewById<Spinner>(R.id.et_gender)

        loginTxt.setOnClickListener {
            onRedirectLogin()
        }

        val calender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year,
                                                              month,
                                                              dayOfMonth ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabelDate(calender)
        }

        etBirth.setOnClickListener {
            DatePickerDialog(
                this, datePicker, calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH), calender.get(
                    Calendar
                        .DAY_OF_MONTH
                )
            ).show()
        }
    }

    private fun updateLabelDate(calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sf = SimpleDateFormat(format, Locale.ENGLISH)

        etBirth.setText(sf.format(calendar.time))
    }

    private fun onRedirectLogin() {
        finish()
    }

    fun onRegister(view: View) {
        val email = etEmail.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val height = etHeight.text.toString().trim()
        val birth = etBirth.text.toString().trim()
        val selectedGender = etGender.selectedItem.toString()
        val emailMatches = !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()

        if (username.isEmpty()) {
            etUsername.error = "Username required"
        }
        if (email.isEmpty()) {
            etEmail.error = "Email required"
        } else if (emailMatches) {
            etEmail.error = "Email must be valid"
        }
        if (password.isEmpty()) {
            etPassword.error = "Password required"
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Confirm Password required"
        } else {
            if (confirmPassword != password) {
                etConfirmPassword.error =
                    "Your password and confirmation password do not match"
            }
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
        if (password.isNotEmpty() && username.isNotEmpty() && confirmPassword
                .isNotEmpty() && email.isNotEmpty() && !emailMatches  &&
            (confirmPassword ==
                    password) && weight.isNotEmpty() && height.isNotEmpty()
            && birth.isNotEmpty()
        ) {
            loading.startLoading()

            requestRegister()
        }
    }

    private fun requestRegister() {
        val rawJson = JSONObject()
        rawJson.put("nama", etUsername.text.toString())
            .put("username", etUsername.text.toString())
            .put("email", etEmail.text.toString())
            .put("password", etPassword.text.toString())
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.create(JSON, rawJson.toString())

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/register")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.isDismiss()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this@RegisterActivity,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }

                if (response.code == 200) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        requestLogin()
                    }
                } else {
                    loading.isDismiss()
                    val msg = json?.getString("detail")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@RegisterActivity,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun requestLogin() {
        val reqbody = RequestBody.create(null, ByteArray(0))
        val request = Request.Builder()
            .url(
                "https://calorties-api-hi7d2j4ixa-et.a.run" +
                        ".app/login?username=" + etUsername.text.toString() +
                        "&password=" + etPassword.text.toString()
            )
            .method("POST", reqbody)
            .header("Content-Length", "0")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.isDismiss()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this@RegisterActivity,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }

                if (response.code == 200) {
                    val token = json?.getString("access_token")
                    if (token != null) {
                        Log.i("TOKEN", token)
                    }

                    val sharedPreference = getSharedPreferences(
                        "CALORTIES",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreference.edit()
                    editor.putString("token", token)
                    editor.putString("username", etUsername.text.toString())
                    editor.apply()

                    registerUser()
                } else {
                    loading.isDismiss()
                    val msg = json?.getString("detail")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@RegisterActivity,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun registerUser() {
        val sharedPreference =
            getSharedPreferences("CALORTIES", Context.MODE_PRIVATE)
        val token = sharedPreference.getString("token", null) ?: ""
        val selectedGender = etGender.selectedItem.toString()

        val rawJson = JSONObject()
        rawJson.put("birthdate", etBirth.text.toString())
            .put("gender", selectedGender)
            .put("tinggi_badan", etHeight.text.toString())
            .put("berat_badan", etWeight.text.toString())

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.create(JSON, rawJson.toString())

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/users")
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()
        Log.i("Register Header", request.headers.toString())
        Log.i("Register Request", request.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }
                Log.i("Register Profile", resStr ?: "")

                loading.isDismiss()
                if (response.code == 200) {
                    val i =
                        Intent(this@RegisterActivity, HomeActivity::class.java)

                    startActivity(i)
                    finish()
                } else {
                    val msg = json?.getString("detail")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@RegisterActivity,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        TODO("Not yet implemented")
    }

}