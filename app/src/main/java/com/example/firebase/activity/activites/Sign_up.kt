package com.example.firebase.activity.activites

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.R
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.Users
import com.example.firebase.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class Sign_up : BaseActivity(){
    var binding:ActivitySignUpBinding?=null
    lateinit var snackbar:Snackbar

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code:Int=123
    var firebaseAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // sign up btn
    binding?.SignUpBtn?.setOnClickListener{
        authentication()
       }

        // this is use to set up the google login service

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // its our web client id
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

    fun UserRegisteredSuccessfully()
    {
        dismissProgressbar()
        Toast.makeText(this,"User Registered Successfully",Toast.LENGTH_SHORT).show()
        // use to provide the data
        firestoreClass().Dataprovider(this)
    }

    // after registering successfully for google
    fun UserRegisteredSuccessfullyGoogle()
    {
        dismissProgressbar()
        Toast.makeText(this,"User Registered Successfully",Toast.LENGTH_SHORT).show()
        // use to provide the data
        finish()
        val intent=Intent(this,profile_Acty::class.java)
        startActivity(intent)

        //firestoreClass().Dataprovider(this)
    }

    // after registering the user firestor will call this func
    fun UserRegisteredSuccessfullyEntry(user:Users)
    {

        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // email authentication starts from here
    private fun authentication()
    {
        val name:String=binding?.name?.text.toString().trim()
        val email:String=binding?.email?.text.toString().trim()
        val pass: String? =binding?.pass?.text?.toString()

        if(isvalid(name,email))
        {
            if (!pass.isNullOrEmpty()) {
                showprogressbar()
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener { Task ->
                    if (Task.isSuccessful) {

                        val firebase: FirebaseUser = Task.result!!.user!!
                        val emails = firebase.email!!

                        // here we have passed the email an uid and name to users model class and passed to fire store class
                        val usr=Users(firebase.uid,name,emails)

                        // this func in fire store class will register in database
                        firestoreClass().registerduser(this,usr)


                    } else {
                        dismissProgressbar()
                        Toast.makeText(this, Task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                dialog("Password field is empty")

            }


        }
    }

    // check before sending the data is empty or filled
    private fun isvalid(name:String,email:String):Boolean
    {
     return when{
         name.isEmpty()->{

             dialog("Please fill the name field")
             false
         }

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
        val parentlayout:View=findViewById(android.R.id.content)
        snackbar=Snackbar.make(parentlayout,message,Snackbar.ANIMATION_MODE_SLIDE)

        snackbar.setAction("Dismiss",View.OnClickListener {
                System.out.println("Set Action on click")
            })

        snackbar.show()


    }

    // sign in google
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
                val firebase: FirebaseUser = task.result!!.user!!
                //Toast.makeText(this,"Sign up done",Toast.LENGTH_SHORT).show()
                //val user=Users(account.idToken!!, name="",email = account.email!!)
                val user=Users(firebase.uid,name="",email = account.email!!)

                // register in data base for the user who want to sign up from the google email
                firestoreClass().registerduserGoogle(this,user)
                //finish()
            }
        }
    }







}