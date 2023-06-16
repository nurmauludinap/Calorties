package com.una.calorties.fragments

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.una.calorties.R
import com.una.calorties.databinding.FragmentHomeBinding
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val client = OkHttpClient()

    lateinit var barChart: BarChart
    lateinit var barData: BarData
    lateinit var barDataSet: BarDataSet
    lateinit var barEntriesList: ArrayList<BarEntry>

//    private val loading = LoadingDialog(this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // on below line we are initializing
        // our variable with their ids.
        barChart = binding.barChart
        getData()

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
//        loading.startLoading()
        getTodayCalorie()
        getThisWeekCalorie()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodayCalorie() {
        val sharedPreference = this.activity?.getSharedPreferences(
            "CALORTIES",
            Context.MODE_PRIVATE
        )
        val token = sharedPreference?.getString("token", null) ?: ""
        val username = sharedPreference?.getString("username", null) ?: ""
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val current = LocalDateTime.now().format(formatter)

        val request = Request.Builder()
            .url("https://calorties-api-hi7d2j4ixa-et.a.run.app/calories/summary-day?date=$current")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                loading.isDismiss()
            }

            override fun onResponse(call: Call, response: Response) {
//                loading.isDismiss()
                val resStr = response.body?.string()

                if (resStr != null) {
                    Log.i("Today Calorie", resStr)
                }

                val json = resStr?.let { JSONObject(it) }
                val masukRaw = json?.getString("total_kalori_masuk")
                val kurangRaw = json?.getString("total_kalori_kurang")
                val berlebihRaw = json?.getString("total_kalori_berlebih")

                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                val masuk = df.format(masukRaw?.toDouble()).toDouble()
                val kurang = df.format(kurangRaw?.toDouble()).toDouble()
                val berlebih = df.format(berlebihRaw?.toDouble()).toDouble()

                if (response.code == 200) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        binding.usernameTxt.text = "Hi, $username"
                        binding.calorieIn.text =
                            "Total kalori yang masuk : $masuk kkal"
                        binding.calorieMin.text =
                            "Total kalori yang kurang : $kurang kkal"
                        binding.calorieExcess.text =
                            "Total kalori yang berlebih : $berlebih kkal"
                    })
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getThisWeekCalorie() {
        val calendar: Calendar = Calendar.getInstance()
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
                    getBarChartData(json ?: JSONArray())

                    if (resStr != null) {
                        Log.i("Weekly Calorie", resStr)
                    }
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBarChartData(data: JSONArray) {
        Handler(Looper.getMainLooper()).post(Runnable {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING

            barEntriesList = ArrayList()

            // on below line we are adding data
            // to our bar entries list
            barEntriesList.add(
                BarEntry(
                    1f,
                    data.getJSONObject(0).getString("total_kalori_masuk")
                        .toFloat()
                )
            )
            barEntriesList.add(
                BarEntry(
                    2f,
                    data.getJSONObject(1).getString("total_kalori_masuk")
                        .toFloat()
                )
            )
            barEntriesList.add(
                BarEntry(
                    3f,
                    data.getJSONObject(2).getString("total_kalori_masuk")
                        .toFloat()
                )
            )
            barEntriesList.add(
                BarEntry(
                    4f,
                    data.getJSONObject(3).getString("total_kalori_masuk")
                        .toFloat()
                )
            )
            barEntriesList.add(
                BarEntry(
                    5f,
                    data.getJSONObject(4).getString("total_kalori_masuk")
                        .toFloat()
                )
            )
            barEntriesList.add(
                BarEntry(
                    6f,
                    data.getJSONObject(5).getString("total_kalori_masuk")
                        .toFloat()
                )
            )
            barEntriesList.add(
                BarEntry(
                    7f,
                    data.getJSONObject(6).getString("total_kalori_masuk")
                        .toFloat()
                )
            )

            setChartData(data)
        })
    }

    private fun setChartData(data: JSONArray) {
        Handler(Looper.getMainLooper()).post(Runnable {
            // on below line we are initializing our bar data set
            barDataSet = BarDataSet(barEntriesList, "Bar Chart Data")

            val labels = ArrayList<String>()
            labels.add(data.getJSONObject(0).getString("date").split("-")[2])
            labels.add(data.getJSONObject(1).getString("date").split("-")[2])
            labels.add(data.getJSONObject(2).getString("date").split("-")[2])
            labels.add(data.getJSONObject(3).getString("date").split("-")[2])
            labels.add(data.getJSONObject(4).getString("date").split("-")[2])
            labels.add(data.getJSONObject(5).getString("date").split("-")[2])
            labels.add(data.getJSONObject(6).getString("date").split("-")[2])

            // on below line we are initializing our bar data
            barData = BarData(barDataSet)

            // on below line we are setting data to our bar chart
            barChart.data = barData
            barChart.invalidate()

            // on below line we are setting colors for our bar chart text
            barDataSet.valueTextColor = Color.BLACK

            // on below line we are setting color for our bar data set
            barDataSet.color = resources.getColor(R.color.secondary_primary)

            // on below line we are setting text size
            barDataSet.valueTextSize = 16f
            barDataSet.setDrawValues(false)

            // on below line we are enabling description as false
            barChart.description.isEnabled = false

            barChart.axisLeft.isEnabled = false
            barChart.axisRight.isEnabled = false
//            barChart.xAxis.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.setBorderColor(255)

            val xAxis: XAxis = barChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE

            val formatter: ValueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels[value.toInt() - 1]
                }
            }

            barChart.xAxis.valueFormatter = formatter
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
        })
    }

}