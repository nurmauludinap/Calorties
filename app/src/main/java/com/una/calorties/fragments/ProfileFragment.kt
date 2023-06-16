package com.una.calorties.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.una.calorties.*
import com.una.calorties.databinding.FragmentProfileBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.privacyTxt.setOnClickListener {
            val intent =
                Intent(requireContext(), AppPrivacyActivity::class.java)
            startActivity(intent)
        }

        binding.changeProfileTxt.setOnClickListener {
            val intent =
                Intent(requireContext(), ChangeProfileActivity::class.java)
            startActivity(intent)
        }

        binding.aboutAppTxt.setOnClickListener {
            val intent = Intent(requireContext(), AboutAppActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener { onLogOut() }

        getData()

        return binding.root
    }

    private fun getData() {
        val sharedPreference = this.activity?.getSharedPreferences(
            "CALORTIES",
            Context.MODE_PRIVATE
        )
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
                    val username = json?.getString("nama")
                    val email = json?.getString("email")
                    val weight = json?.getString("berat_badan")
                    val height = json?.getString("tinggi_badan")
                    val gender = json?.getString("gender")
                    Handler(Looper.getMainLooper()).post(Runnable {
                        binding.usernameTxt.text = username
                        binding.emailTxt.text = email
                        binding.bbTxt.text = "TB: $height | BB: $weight | Gender: $gender"
                    })
                }
            }
        })
    }

    private fun onLogOut() {
        val sharedPreference =
            this.activity?.getSharedPreferences(
                "CALORTIES", Context
                    .MODE_PRIVATE
            )
        val editor = sharedPreference?.edit()
        editor?.clear()
        editor?.apply()

        val i = Intent(requireContext(), MainActivity::class.java)

        activity?.finish()
        startActivity(i)
    }
}