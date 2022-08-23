package com.example.firebase.activity.activites

import android.app.Activity
import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.R
import com.example.firebase.activity.Adapter.MembersAdapter
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.Board
import com.example.firebase.activity.models.Users
import com.example.firebase.activity.models.const
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.edti_info_file.view.data_member_email_add
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class Members_Activity : BaseActivity(){

    lateinit var board:Board
    lateinit var memberlist:ArrayList<Users>
    var needUpdate:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        setupAcitonBar()


        if(intent.hasExtra(const.board_details))
        {
            board= intent.getParcelableExtra<Board>(const.board_details)!!
        }
        showprogressbar()
        firestoreClass().membersDetails(this@Members_Activity,board.assignedto)
    }

    /** for action bar**/
    private fun setupAcitonBar()
    {
        setSupportActionBar(toolbar_member_activity)
        val actionbar=supportActionBar
        if(actionbar !=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            toolbar_member_activity.setNavigationOnClickListener {
                onBackPressed()
            }

        }
        Log.i("tlbr_mbr","toolbar")
    }
    /** to set the adapter*/
    fun setupAdpater(list:ArrayList<Users>)
    {
        memberlist=list
        rv_members.layoutManager=LinearLayoutManager(this)
        val adpter=MembersAdapter(this@Members_Activity,list)
        rv_members.adapter=adpter
    }

    /** this is used to set button in toolbar*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.task_members_btn->{
                dialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialog()
    {


        /**
         * The LayoutInflater class is used to instantiate the contents of layout XML files into their corresponding View objects.
         * In other words, it takes an XML file as input and builds the View objects from it.
         * if you want a view from a xml use inflater
         */


        val alertDialog = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.members_add_info, null)

        alertDialog.setCancelable(false)
        alertDialog.setView(dialogView)


        val btOk = dialogView.findViewById(R.id.save_edit_btn_mem) as Button
        val btCancel = dialogView.findViewById(R.id.cancel_edit_btn_mem) as Button

        btOk.setOnClickListener(View.OnClickListener{
            val text=dialogView.data_member_email_add.text.toString()
            if(text.isNotEmpty())
            {

                showprogressbar()
                alertDialog.dismiss()
                firestoreClass().getMembersDetails(this,text)

            }
            else{
                Toast.makeText(this,"Email field is empty",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        })
        btCancel.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Edit dialog closed..!!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        })
        alertDialog.show()
    }

    /** this will recieve the user data*/
    fun ReciveUserData(usr:Users)
    {
        board.assignedto.add(usr.id)
        // to update the board member that has been added
        firestoreClass().updateMemberdata(this,board,usr)
    }

    /** it will receive the new added board member and send to adapter to update it */
    fun suceesUpdateMember(users: Users)
    {
        memberlist.add(users)
        // to update the UI
        needUpdate=true
        setupAdpater(memberlist)
        sendNotifications(board.boardname,users.fcmToken).execute()
    }

    /** this will set result ok bec this will tell that yes we need update */
    override fun onBackPressed() {
        if(needUpdate){
            Activity.RESULT_OK
        }
        super.onBackPressed()
    }

    /**
     * This function will be used to perform background execution.
     */
    private inner class sendNotifications(val boardname:String , val Token:String):AsyncTask<Any,Void,String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            showprogressbar()
        }
        override fun doInBackground(vararg p0: Any?): String {
           var result:String
           var connection:HttpURLConnection?=null
            try{
                val url=URL(const.FCM_BASE_URL)
                connection=url.openConnection() as HttpURLConnection
                connection.doOutput=true
                connection.instanceFollowRedirects=false
                connection.requestMethod="POST"

                connection.setRequestProperty(
                    const.FCM_AUTHORIZATION, "${const.FCM_KEY}=${const.FCM_SERVER_KEY}")

                    /**
                     * Creates a new data output stream to write data to the specified
                     * underlying output stream. The counter written is set to zero.
                     */
                    val wr = DataOutputStream(connection.outputStream)

                // TODO (Step 4: Create a notification data payload.)
                // START
                // Create JSONObject Request
                val jsonRequest = JSONObject()

                // Create a data object
                val dataObject = JSONObject()
                // Here you can pass the title as per requirement as here we have added some text and board name.
                dataObject.put( const.FCM_KEY_TITLE, "Assigned to the Board ${boardname}")
                // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                dataObject.put(
                    const.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by ${memberlist[0].name}"
                )
                jsonRequest.put(const.FCM_KEY_DATA,dataObject)
                jsonRequest.put(const.FCM_KEY_TO,Token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult:Int=connection.responseCode
                if(httpResult==HttpURLConnection.HTTP_OK)
                {
                    val inputstream=connection.inputStream
                    val reader=BufferedReader(InputStreamReader(inputstream))

                    val sb=StringBuilder()
                    var line:String?
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    }catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            /**
                             * Closes this input stream and releases any system resources associated
                             * with the stream.
                             */
                            inputstream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                }
                else {
                    /**
                     * Gets the HTTP response message, if any, returned along with the
                     * response code from a server.
                     */
                    result = connection.responseMessage
                }

            }catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }


           return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            dismissProgressbar()
        }

    }
}