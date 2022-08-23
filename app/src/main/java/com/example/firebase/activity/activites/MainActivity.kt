package com.example.firebase.activity.activites

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.activity.Adapter.BoardAdapter
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.Board
import com.example.firebase.activity.models.Users
import com.example.firebase.activity.models.const
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_barmain.*
import kotlinx.android.synthetic.main.flotating_btn.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*


// here we have set navigation item on click listner by inherting  navig..
class MainActivity : BaseActivity() {

    companion object{
        const val sucees_request_code:Int=11
        const val sucess_req_board_lst:Int=12
        const val ONESIGNAL_APP_ID="1e100a66-5821-4452-82c4-a6a22aee2a46"
    }

    private lateinit var shardpref:SharedPreferences

    lateinit var mUserName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        if(update!=1)
        {
            update=1
        }
        // this is used to declare shared pref
        shardpref=this.getSharedPreferences(const.Project_Md_sharedPref, Context.MODE_PRIVATE)
        val tokenupdate=shardpref.getBoolean(const.fcm_token_Update,false)




        if(tokenupdate)
        {
            //showprogressbar()
            firestoreClass().Dataprovider(this,true)
        }else{
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String?> ->
                    if (!task.isSuccessful) {
                        //Could not get FirebaseMessagingToken
                        return@addOnCompleteListener
                    }
                    if (null != task.result) {
                        //Got FirebaseMessagingToken
                        val firebaseMessagingToken= Objects.requireNonNull(task.getResult())
                        fcmtokenUpdate(firebaseMessagingToken.toString())
                        Log.i("fcm",firebaseMessagingToken.toString())
                        //Use firebaseMessagingToken further
                    }
                }
        }

        /** for one signal notifications*/
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        actionbar()
        // by doing we are able to click on the navigation item
        Navigation.setNavigationItemSelectedListener { MenuItem ->
            when(MenuItem.itemId)
            {
                R.id.profile_options -> {
                    //Toast.makeText(this,"done and dusted",Toast.LENGTH_SHORT).show()
                    val intent=Intent(this,profile_Acty::class.java)
                    startActivityForResult(intent, sucees_request_code)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.SignOutOpt -> {
                    FirebaseAuth.getInstance().signOut()
                    shardpref.edit().clear().apply()
                    val intent= Intent(this,LoginIntro::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }

                else -> {
                    false
                }
            }

        }
        firestoreClass().Dataprovider(this,boardupdate = true)
        floating_board.setOnClickListener {
            val intent=Intent(this,BoardActivity::class.java)
            intent.putExtra(const.username,mUserName)

            /** very important point this for board starting board adding detail and creating but we will add activtiyforresult
             * so to  update the list of the board in main we will use start act for reuslt
             * here we are passing the code to board act
             */
            startActivityForResult(intent, sucess_req_board_lst)
        }
    }

    private fun actionbar()
    {
        setSupportActionBar(tool_bar_board)
        tool_bar_board.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        tool_bar_board.setNavigationOnClickListener {
            toogledrawer()
        }
    }

    private fun toogledrawer()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        }

        else
        {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        }

        else
        {
            doubleBackExit()
        }

    }






    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK && requestCode== sucees_request_code)
        {
            firestoreClass().Dataprovider(this)
        }
        else if(resultCode==Activity.RESULT_OK && requestCode== sucess_req_board_lst)
        {
            firestoreClass().Dataprovider(this,true)
        }
        else{
            Log.e("error","something went wrong")
        }
    }

    // here data is provided by the firestore class
    fun updateNavigationUserData(loggedinuser: Users,boardlist:Boolean) {
        mUserName=loggedinuser.name
        User_name_navi.text=loggedinuser.name
        Glide
            .with(this)
            .load(loggedinuser.image)
            .fitCenter()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(navi_image);

        if(boardlist)
        {
            //showprogressbar()
            // called when want to show board list
            firestoreClass().getboardata(this)
        }
    }
    // for Board
    fun DisplayBoads(boards:ArrayList<Board>)
    {
        //dismissProgressbar()
        if(boards.size > 0)
        {
            recylerview_board.visibility= View.VISIBLE
            No_boards_ava.visibility=View.GONE

            recylerview_board.layoutManager=LinearLayoutManager(this)
            val adapter=BoardAdapter(this,boards)
            recylerview_board.adapter=adapter
            adapter.setonclicklistner(object :BoardAdapter.OnClicklistner{
                override fun onClick(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskActivityList::class.java)
                    intent.putExtra(const.documentid,model.documentedId)
                    //intent.putExtra(const.updateval,update)
                    startActivity(intent)
                }

            })

        }
        else{
            recylerview_board.visibility= View.GONE
            No_boards_ava.visibility=View.VISIBLE
        }
    }

    // this will called when you have added the token to the dbase
    // this will set the shared pref value
    fun onTokenSucces() {
        //dismissProgressbar()
        val editor:SharedPreferences.Editor=shardpref.edit()
        editor.putBoolean(const.fcm_token_Update,true)
        editor.apply()
        //firestoreClass().Dataprovider(this,true)



    }

    // to update the token or add in the data base
    // rember one thing if you want to update something use the ultimate hash map
    private fun fcmtokenUpdate(token:String)
    {
        val tokens=HashMap<String,Any>()
        tokens[const.fcmToken]=token
        //showprogressbar()
        //firestoreClass().udpateUserProfile(this,tokens)
    }
    override fun onResume() {
        //showprogressbar()
        firestoreClass().Dataprovider(this,true)
        super.onResume()
    }

}