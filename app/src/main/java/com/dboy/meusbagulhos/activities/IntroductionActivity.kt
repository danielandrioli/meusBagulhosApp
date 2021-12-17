package com.dboy.meusbagulhos.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.dboy.meusbagulhos.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide

class IntroductionActivity : IntroActivity() {
    private val SHARED_PREF = "intro_app"
    private val preferences by lazy { getSharedPreferences(SHARED_PREF, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferences.contains("isIntroSeen")){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            addingSlides()
        }
    }

    fun initBtn(view: View) {
        val editor = preferences.edit()
        editor.putBoolean("isIntroSeen", true)
        editor.commit()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addingSlides() {
        isButtonBackVisible = false
        isButtonNextVisible = false

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.background_intro)
                .fragment(R.layout.intro_01)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.background_intro)
                .fragment(R.layout.intro_02)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.background_intro)
                .fragment(R.layout.intro_03)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.background_intro)
                .fragment(R.layout.intro_04)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.background_intro)
                .fragment(R.layout.intro_05)
                .canGoForward(false)
                .build()
        )
    }
}