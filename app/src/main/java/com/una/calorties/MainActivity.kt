package com.una.calorties

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.una.calorties.fragments.LoadingDialog
import com.una.calorties.fragments.RegisteredDialog
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val loading = LoadingDialog(this)
    private val registeredDialog = RegisteredDialog(this)
    private val client = OkHttpClient()

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerTxt = findViewById(R.id.register_txt) as TextView
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)

        registerTxt.setOnClickListener {
            onRedirectRegister()
        }
    }

    private fun showAlertDialog() {
        registeredDialog.show()
    }

    fun dismissRegisterDialog(view: View) {
        registeredDialog.isDismiss()
    }

    fun onLogin(view: View) {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = "Username required"
        }
        if (password.isEmpty()) {
            etPassword.error = "Password required"
        }
        if (password.isNotEmpty() && username.isNotEmpty()) {
            loading.startLoading()

            requestLogin()
        }
    }

    private fun onRedirectRegister() {
        val i = Intent(this, RegisterActivity::class.java)

        startActivity(i)
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
                        this@MainActivity,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                loading.isDismiss()
                val resStr = response.body?.string()
                val json = resStr?.let { JSONObject(it) }

                if (response.code == 200) {
                    val token = json?.getString("access_token")
                    if (token != null) {
                        Log.i("TOKEN", token)
                    }

                    val sharedPreference =  getSharedPreferences("CALORTIES",
                        Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    editor.putString("token", token)
                    editor.putString("username", etUsername.text.toString())
                    editor.apply()

                    val i = Intent(this@MainActivity, HomeActivity::class.java)

                    startActivity(i)
                    finish()
                } else {
                    val msg = json?.getString("detail")
                    Handler(Looper.getMainLooper()).post {
                        showAlertDialog()
                    }
                }
            }
        })
    }
}