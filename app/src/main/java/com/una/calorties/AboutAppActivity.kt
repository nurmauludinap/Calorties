package com.una.calorties

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

class AboutAppActivity : AppCompatActivity() {
    private var isExpanded1 = false
    private var isExpanded2 = false
    private var isExpanded3 = false
    private var isExpanded4 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val backButton: ImageButton = findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val cardView1: CardView = findViewById(R.id.cardView1)
        val titleContentLayout1: View = findViewById(R.id.cardTitleTxt1)
        val hiddenContentLayout1: View = findViewById(R.id.hiddenContent1)
        val separator1: View = findViewById(R.id.separatorLine)

        cardView1.setOnClickListener {
            isExpanded1 = !isExpanded1
            hiddenContentLayout1.visibility =
                if (isExpanded1) View.VISIBLE else View.GONE
            separator1.visibility = if (!isExpanded1) View.VISIBLE else View.GONE

            val color = ContextCompat.getColor(this, R.color.white)
            val activeColor =
                ContextCompat.getColor(this, R.color.secondary_primary)
            val opacity = 128
            val colorWithOpacity =
                ColorUtils.setAlphaComponent(activeColor, opacity)

            hiddenContentLayout1.setBackgroundColor(
                if (!isExpanded1) color
                else colorWithOpacity
            )
            titleContentLayout1.setBackgroundColor(
                if (!isExpanded1) color
                else activeColor
            )
        }

        val cardView2: CardView = findViewById(R.id.cardView2)
        val titleContentLayout2: View = findViewById(R.id.cardTitleTxt2)
        val hiddenContentLayout2: View = findViewById(R.id.hiddenContent2)
        val separator2: View = findViewById(R.id.separatorLine2)

        cardView2.setOnClickListener {
            isExpanded2 = !isExpanded2
            hiddenContentLayout2.visibility =
                if (isExpanded2) View.VISIBLE else View.GONE
            separator2.visibility = if (!isExpanded2) View.VISIBLE else View.GONE

            val color = ContextCompat.getColor(this, R.color.white)
            val activeColor =
                ContextCompat.getColor(this, R.color.secondary_primary)
            val opacity = 128
            val colorWithOpacity =
                ColorUtils.setAlphaComponent(activeColor, opacity)

            hiddenContentLayout2.setBackgroundColor(
                if (!isExpanded2) color
                else colorWithOpacity
            )
            titleContentLayout2.setBackgroundColor(
                if (!isExpanded2) color
                else activeColor
            )
        }

        val cardView3: CardView = findViewById(R.id.cardView3)
        val titleContentLayout3: View = findViewById(R.id.cardTitleTxt3)
        val hiddenContentLayout3: View = findViewById(R.id.hiddenContent3)
        val separator3: View = findViewById(R.id.separatorLine3)

        cardView3.setOnClickListener {
            isExpanded3 = !isExpanded3
            hiddenContentLayout3.visibility =
                if (isExpanded3) View.VISIBLE else View.GONE
            separator3.visibility = if (!isExpanded3) View.VISIBLE else View.GONE

            val color = ContextCompat.getColor(this, R.color.white)
            val activeColor =
                ContextCompat.getColor(this, R.color.secondary_primary)
            val opacity = 128
            val colorWithOpacity =
                ColorUtils.setAlphaComponent(activeColor, opacity)

            hiddenContentLayout3.setBackgroundColor(
                if (!isExpanded3) color
                else colorWithOpacity
            )
            titleContentLayout3.setBackgroundColor(
                if (!isExpanded1) color
                else activeColor
            )
        }

        val cardView4: CardView = findViewById(R.id.cardView4)
        val titleContentLayout4: View = findViewById(R.id.cardTitleTxt4)
        val hiddenContentLayout4: View = findViewById(R.id.hiddenContent4)
        val separator4: View = findViewById(R.id.separatorLine4)

        cardView4.setOnClickListener {
            isExpanded4 = !isExpanded4
            hiddenContentLayout4.visibility =
                if (isExpanded4) View.VISIBLE else View.GONE
            separator4.visibility = if (!isExpanded4) View.VISIBLE else View.GONE

            val color = ContextCompat.getColor(this, R.color.white)
            val activeColor =
                ContextCompat.getColor(this, R.color.secondary_primary)
            val opacity = 128
            val colorWithOpacity =
                ColorUtils.setAlphaComponent(activeColor, opacity)

            hiddenContentLayout4.setBackgroundColor(
                if (!isExpanded4) color
                else colorWithOpacity
            )
            titleContentLayout4.setBackgroundColor(
                if (!isExpanded4) color
                else activeColor
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}