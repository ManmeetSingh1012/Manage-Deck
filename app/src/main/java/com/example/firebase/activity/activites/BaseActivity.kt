package com.example.firebase.activity.activites

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.firebase.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    private var doubleBackpressed=false
    lateinit var progressdialog:Dialog
    var update :Int =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showprogressbar()
    {
        progressdialog= Dialog(this)
        progressdialog.setContentView(R.layout.progress_bar)
        progressdialog.show()
    }

    fun dismissProgressbar()
    {
        progressdialog.dismiss()
    }

    fun getcurrentuserid():String
    {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackExit()
    {
        if(doubleBackpressed)
        {
            super.onBackPressed()
        }

        this.doubleBackpressed=true
        Toast.makeText(this,"Please click back button again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackpressed=false
        },3000)
    }

    fun dialogSnackbar(message:String)
    {
        val parentlayout: View =findViewById(android.R.id.content)
        val snackbar=Snackbar.make(parentlayout,message,Snackbar.ANIMATION_MODE_SLIDE)

        snackbar.setAction("Dismiss", View.OnClickListener {
            System.out.println("Set Action on click")
        })

        snackbar.show()


    }

    /*fun showerrortouser(message:String)
    {
        val snackbar= Snackbar.make(findViewById(R.id.content),
            message, Snackbar.LENGTH_SHORT)

        snackbar.view.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
        snackbar.show()
    }*/
}