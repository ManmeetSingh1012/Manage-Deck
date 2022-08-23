package com.example.firebase.activity.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.firebase.R
import com.example.firebase.activity.Firestore.firestoreClass

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        // it is used to delay the logo by few sec
        Handler(Looper.getMainLooper()).postDelayed({

            if(firestoreClass().getCurrUserId().isNotEmpty())
            {
                val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this, LoginIntro::class.java)
                startActivity(intent)
                finish()
            }
        },1000)
    }
}