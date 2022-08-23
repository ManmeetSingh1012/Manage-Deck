package com.example.firebase.activity.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.firebase.R

class LoginIntro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_intro)

// this will lead you to the sign up activity
        val btn: Button =findViewById(R.id.signup)
        btn.setOnClickListener {
            val intent= Intent(this, Sign_up::class.java)
            startActivity(intent)

        }


        // this will lead you to the sign in activity
        val btn2: Button =findViewById(R.id.signin)
        btn2.setOnClickListener {
            val intent= Intent(this, SignInActivity::class.java)
            startActivity(intent)

        }
    }
}