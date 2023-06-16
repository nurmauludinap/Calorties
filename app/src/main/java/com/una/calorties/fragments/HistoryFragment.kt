package com.una.calorties.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.una.calorties.databinding.FragmentHistoryBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryFragment : Fragment() {
    private val client = OkHttpClient()
    private lateinit var binding: FragmentHistoryBinding
    lateinit var content1: TextView
    lateinit var content2: TextView
    lateinit var content3: TextView
    lateinit var content4: TextView
    lateinit var content5: TextView
    lateinit var content6: TextView
    lateinit var content7: TextView
    lateinit var date1: TextView
    lateinit var date2: TextView
    lateinit var date3: TextView
    lateinit var date4: TextView
    lateinit var date5: TextView
    lateinit var date6: TextView
    lateinit var date7: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentHistoryBinding.inflate(layoutInflater, container, false)

        content1 = binding.cardContent1
        content2 = binding.cardContent2
        content3 = binding.cardContent3
        content4 = binding.cardContent4
        content5 = binding.cardContent5
        content6 = binding.cardContent6
        content7 = binding.cardContent7
        date1 = binding.cardDate1
        date2 = binding.cardDate2
        date3 = binding.cardDate3
        date4 = binding.cardDate4
        date5 = binding.cardDate5
        date6 = binding.cardDate6
        date7 = binding.cardDate7

        getThisWeekCalorie()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getThisWeekCalorie() {
        val sharedPreference = this.activity?.getSharedPreferences(
            "CALORTIES",
            Context.MODE_PRIVATE
        )
        val token = sharedPreference?.getString("token", null) ?: ""
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val beforeDate = LocalDateTime.now().minusDays(6).format(formatter)
        val current = LocalDateTime.now().format(formatter)

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/calories/summary-week?start_date=$beforeDate&end_date=$current")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                loading.isDismiss()
            }

            override fun onResponse(call: Call, response: Response) {
//                loading.isDismiss()
                val resStr = response.body?.string()
                val json = resStr?.let { JSONArray(it) }

                if (response.code == 200) {
                    populateData(json ?: JSONArray())
                }
            }
        })
    }

    private fun populateData(data: JSONArray) {
        Handler(Looper.getMainLooper()).post(Runnable {
            content1.text =
                data.getJSONObject(0).getString("total_kalori_masuk") + " kkal"
            content2.text =
                data.getJSONObject(1).getString("total_kalori_masuk") + " kkal"
            content3.text =
                data.getJSONObject(2).getString("total_kalori_masuk") + " kkal"
            content4.text =
                data.getJSONObject(3).getString("total_kalori_masuk") + " kkal"
            content5.text =
                data.getJSONObject(4).getString("total_kalori_masuk") + " kkal"
            content6.text =
                data.getJSONObject(5).getString("total_kalori_masuk") + " kkal"
            content7.text =
                data.getJSONObject(6).getString("total_kalori_masuk") + " kkal"

            val formatIncoming = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            val formatOutgoing = SimpleDateFormat("EEEE, dd MMMM yyyy")
            val tz = TimeZone.getTimeZone("Asia/Jakarta")
            formatOutgoing.timeZone = tz

            val dateRes1 =
                formatIncoming.parse(data.getJSONObject(0).getString("date"))
                    ?.let { formatOutgoing.format(it) }
            val dateRes2 = formatIncoming.parse(
                data.getJSONObject(1)
                    .getString("date")
            )
                ?.let { formatOutgoing.format(it) }
            val dateRes3 = formatIncoming.parse(
                data.getJSONObject(2)
                    .getString("date")
            )
                ?.let { formatOutgoing.format(it) }
            val dateRes4 = formatIncoming.parse(
                data.getJSONObject(3)
                    .getString("date")
            )
                ?.let { formatOutgoing.format(it) }
            val dateRes5 = formatIncoming.parse(
                data.getJSONObject(4)
                    .getString("date")
            )
                ?.let { formatOutgoing.format(it) }
            val dateRes6 = formatIncoming.parse(
                data.getJSONObject(5)
                    .getString("date")
            )
                ?.let { formatOutgoing.format(it) }
            val dateRes7 = formatIncoming.parse(
                data.getJSONObject(6)
                    .getString("date")
            )
                ?.let { formatOutgoing.format(it) }

            date1.text = dateRes1
            date2.text = dateRes2
            date3.text = dateRes3
            date4.text = dateRes4
            date5.text = dateRes5
            date6.text = dateRes6
            date7.text = dateRes7

            binding.apply {
                calorieCard1.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(
                            data
                                .getJSONObject(0).getString("date")?:"")
                    findNavController()
                        .navigate(direction)
                }
                calorieCard2.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(data
                            .getJSONObject(1).getString("date")?:"")
                    findNavController()
                        .navigate(direction)
                }
                calorieCard3.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(data
                            .getJSONObject(2).getString("date")?:"")
                    findNavController()
                        .navigate(direction)
                }
                calorieCard4.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(data
                            .getJSONObject(3).getString("date")?:"")
                    findNavController()
                        .navigate(direction)
                }
                calorieCard5.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(data
                            .getJSONObject(4).getString("date")?:"")


                    findNavController()
                        .navigate(direction)
                }
                calorieCard6.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(data
                            .getJSONObject(5).getString("date")?:"")
                    findNavController()
                        .navigate(direction)
                }
                calorieCard7.setOnClickListener {
                    val direction = HistoryFragmentDirections
                        .actionHistoryFragmentToCalorieDetailActivity2(data
                            .getJSONObject(6).getString("date")?:"")
                    findNavController()
                        .navigate(direction)
                }
            }
        })
    }
}