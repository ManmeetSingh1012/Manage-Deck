package com.example.firebase.activity.activites

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.firebase.R
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.Users
import com.example.firebase.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : BaseActivity(){

    var binding:ActivitySignInBinding?=null
    lateinit var snackbar: Snackbar

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code:Int=123
    var firebaseAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        binding?.login?.setOnClickListener {
            authentication()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id2))
            .requestEmail()
            .build()
// getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)
// initialize the firebaseAuth variable
        firebaseAuth= FirebaseAuth.getInstance()

        binding?.google?.setOnClickListener {
            signInGoogle()

        }

    }

    fun UserRegisteredSuccessfully(user:Users)
    {
        dismissProgressbar()
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun authentication()
    {

        val email:String=binding?.email2?.text.toString().trim()
        val pass: String? =binding?.pass2?.text?.toString()

        val auth= FirebaseAuth.getInstance()
        Log.i("check","done")
        if(isvalid(email))
        {
            if (!pass.isNullOrEmpty()) {
                showprogressbar()
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information


                            Log.d("check1", "signInWithEmail:success")
                            val user = auth.currentUser
                            firestoreClass().Dataprovider(this)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("check2", "signInWithEmail:failure", task.exception)
                            dismissProgressbar()
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            }


        }
    }


    private fun isvalid(email:String):Boolean
    {
        return when{
            TextUtils.isEmpty(email)->{
                dialog("Email field is empty")
                false
            }

            else->{
                true

            }
        }
    }

    private fun dialog(message:String)
    {
        val parentlayout: View =findViewById(android.R.id.content)
        snackbar= Snackbar.make(parentlayout,message, Snackbar.ANIMATION_MODE_SLIDE)

        snackbar.setAction("Dismiss", View.OnClickListener {
            System.out.println("Set Action on click")
        })

        snackbar.show()

    }






    private  fun signInGoogle(){


        val signInIntent: Intent =mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent,Req_Code)
    }
    // onActivityResult() function : this is where we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                showprogressbar()
                UpdateUI(account)
            }
        } catch (e: ApiException){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }
    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                dismissProgressbar()
                //Toast.makeText(this,"Sign in done",Toast.LENGTH_SHORT).show()
                //finish()
                firestoreClass().DataproviderForGoogle(this)
            }
        }
    }

    /*override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }*/
}