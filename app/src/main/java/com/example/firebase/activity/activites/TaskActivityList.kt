package com.example.firebase.activity.activites

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.R
import com.example.firebase.activity.Adapter.TaskAdapter
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.*
import kotlinx.android.synthetic.main.activity_task_list.*
import org.intellij.lang.annotations.JdkConstants
import java.text.FieldPosition

class TaskActivityList : BaseActivity() {
    lateinit var documentid:String
    private lateinit var mboards: Board
    //private lateinit var AssMemberslist:ArrayList<Users>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if(intent.hasExtra(const.documentid))
        {
            documentid= intent.getStringExtra(const.documentid).toString()
            Log.i("dcid2",documentid)
        }
        //showprogressbar()
        firestoreClass().boardTasklist(this,documentid)
    }


    fun tasklist(model: Board)
    {
        //dismissProgressbar()
        mboards=model
        setupActionbar(model.boardname)


        val task=Task("Add Task")
        model.taskslist.add(task)

        recyelerview_task.visibility=View.VISIBLE
        recyelerview_task.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,
            false)

       //recyelerview_task.setHasFixedSize(true)



        // here we are setting the item decoration by calling the

        if(update==1) {
            recyelerview_task.addItemDecoration(
                TaskAdapter.MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.margin))
            )
        }
        val adapter=TaskAdapter(this,model.taskslist)
        recyelerview_task.adapter=adapter
        update=0


    }

    private fun setupActionbar(boardname: String) {
        setSupportActionBar(toolbar_task)
        val actionbar=supportActionBar
        if(actionbar!=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.title = boardname

            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            toolbar_task.setNavigationOnClickListener {
                onBackPressed()
            }

        }
    }

    // here we have taken the name from the adapter and created new func in firestore and passed values to it
    fun createTaskList(tasklistname:String)
    {
        Log.i("task","every thing is fine")

        val newtask:Task=Task(tasklistname,firestoreClass().getCurrUserId())
        mboards.taskslist.add(0,newtask)

        /* why we are removing the last bec it contains the Add task text if we will not do then it
           when we update it then it will again add it one will be the list we will add it again at the time of loading
           so it will add up in the end but the end we will display the add list btn */
        mboards.taskslist.removeAt(mboards.taskslist.size-1)

        showprogressbar()
        firestoreClass().boardTasklistupdate(this@TaskActivityList,mboards)

    }

    // after adding list successfully we will update it as well
    fun addUpdateTaskListSuccess() {

        dismissProgressbar()
        dialogSnackbar("Updated Successfully ")
        // Here get the updated board details.
        // Show the progress dialog.
        //showprogressbar()
        firestoreClass().boardTasklist(this@TaskActivityList, mboards.documentedId)
    }



    fun deletelistname(model:Task)
    {
        mboards.taskslist.remove(model)
        mboards.taskslist.removeAt(mboards.taskslist.size-1)
        showprogressbar()
        firestoreClass().boardTasklistupdate(this@TaskActivityList,mboards)
    }

    fun editTaskListTitle(tasktitle:String,position: Int,model: Task)
    {
        val edittask=Task(tasktitle,model.createdby,model.cardlist)
        mboards.taskslist[position]=edittask
        mboards.taskslist.removeAt(mboards.taskslist.size-1)
        showprogressbar()
        firestoreClass().boardTasklistupdate(this@TaskActivityList,mboards)
    }

    /** for card*/

    fun addcards(title:String,position: Int)
    {
        mboards.taskslist.removeAt(mboards.taskslist.size-1)

        /** created the new card*/
        var listCardAssgnd:ArrayList<String> = ArrayList()
        listCardAssgnd.add(firestoreClass().getCurrUserId())

        val newCard=Card(title,firestoreClass().getCurrUserId(),listCardAssgnd)

        val cardlist=mboards.taskslist[position].cardlist
        cardlist.add(newCard)

        val newtask=Task( mboards.taskslist[position].title,mboards.taskslist[position].createdby,cardlist)
        mboards.taskslist[position]=newtask


        showprogressbar()
        firestoreClass().boardTasklistupdate(this@TaskActivityList,mboards)
    }

    /** this func will display three dots at the top right corner in toolbar and we can perform diff func*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.all_task_mem,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.task_mem_btn ->{
                val intent=Intent(this,Members_Activity::class.java)
                intent.putExtra(const.board_details,mboards)
                /** Because sometimes after adding the member, member added something to board but it will not visible to others
                 * so to make visible to others we want to update the board activity when member go out of the member activity
                 */
                startActivityForResult(intent, Members_Request_Code)
            }


            R.id.delete_board->{
                deleteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }


/* this will update the ui if there is any change done in members activity or card details activity*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_OK && (requestCode== Members_Request_Code || requestCode == CardDetailsCode) )
        {
            firestoreClass().boardTasklist(this,documentid)
        }
        else{
            Log.i("For updating ","Updating the data after adding the member")
        }
    }

    /** this will help us to send  details of the the card and task so that we can edit it */
    fun cardDetails(position: Int, cardPosition: Int) {
        val intent=Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(const.board_details,mboards)
        intent.putExtra(const.taskNo,position)
        intent.putExtra(const.cardNo,cardPosition)
        //intent.putExtra(const.Members_Data,AssMemberslist)
        startActivityForResult(intent, CardDetailsCode)
    }

    /**
     * this (task activity update) also can be done using this method
     */

    override fun onResume() {
        //showprogressbar()
        firestoreClass().boardTasklist(this,documentid)
        super.onResume()
    }

    // this will recive the data of users that we will send to card detail activity which we will use to assign the member to the card
    /*fun gettingMemberList(mber:ArrayList<Users>)
    {
        AssMemberslist=mber

    }*/

    companion object{
        const val Members_Request_Code=13
        const val CardDetailsCode=14
    }
    fun deleteDialog()
    {
        val dialog= AlertDialog.Builder(this)
        dialog.setTitle("Delete the Board")
        dialog.setMessage("Are your Sure that you want to delete the card")
        dialog.setNegativeButton("No"){

                dialog,wich->
            dialog.dismiss()
            Toast.makeText(this,"Hmm!You changed your mind", Toast.LENGTH_SHORT).show()

        }
        dialog.setPositiveButton("Yes"){
                dialog,which->DeleteCard()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.create().show()

    }

    private fun DeleteCard()
    {
        firestoreClass().boardTasklistDelete(this,mboards)
    }

    fun DeleteSuccess() {


        Toast.makeText(this,"Board deleted successfully",Toast.LENGTH_SHORT).show()
        finish()
        // Here get the updated board details.
        // Show the progress dialog.
        //showprogressbar()
        //firestoreClass().boardTasklist(this@TaskActivityList, mboards.documentedId)
    }

}