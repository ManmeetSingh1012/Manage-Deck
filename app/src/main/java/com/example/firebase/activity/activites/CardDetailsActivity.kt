package com.example.firebase.activity.activites

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.R
import com.example.firebase.activity.Adapter.SelectedMembersAdapter
import com.example.firebase.activity.Dialogs.ColorDailogClass
import com.example.firebase.activity.Dialogs.MembersDialogClass
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.*
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    lateinit var board: Board
    private var taskPost:Int=-1
    private var cardPost:Int=-1

    private var mslectedColor=""
    private var mslecteddate:String=""
    lateinit var MembersDataList:ArrayList<Users>


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        intentData()
        setActionBar()
        et_name_card_details.setText(board.taskslist[taskPost].cardlist[cardPost].title)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)



        MembersData()
        mslectedColor=board.taskslist[taskPost].cardlist[cardPost].mslectedColor
        if(mslectedColor.isNotEmpty())
        {
            setcolor()
        }

        //selectedMembersAdpterSetup()
        tv_select_label_color.setOnClickListener {
            colordialog()
        }
        tv_select_members.setOnClickListener {
            showMembersDialog()
        }
        tv_date_selcted.setOnClickListener {
            DatePicker()
        }
        card_update_btn.setOnClickListener { updateData() }

        //mslecteddate=
            //board.taskslist[taskPost].cardlist[cardPost].DueDate

        if (board.taskslist[taskPost].cardlist[cardPost].DueDate.isNotEmpty()) {
            Log.i("dueDate", board.taskslist[taskPost].cardlist[cardPost].DueDate)
            tv_date_selcted.text=board.taskslist[taskPost].cardlist[cardPost].DueDate
        }



    }
    private fun MembersData()
    {
        showprogressbar()
        firestoreClass().membersDetails(this@CardDetailsActivity,board.assignedto)
    }
    fun gettingMemberList(mber:ArrayList<Users>)
    {
        MembersDataList=mber
        Log.i("Board member",  MembersDataList.toString())
        Log.i("Board member2", "nothing ")
        selectedMembersAdpterSetup()



    }

    /* creating the on create options*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_detail_act,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.delete_card_btn->{
                deleteDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // geting the intent data
    fun intentData()
    {
        if(intent.hasExtra(const.board_details))
        {
            board= intent.getParcelableExtra<Board>(const.board_details)!!
        }
        if(intent.hasExtra(const.cardNo))
        {
            cardPost=intent.getIntExtra(const.cardNo,-1)
        }
        if(intent.hasExtra(const.taskNo))
        {
            taskPost=intent.getIntExtra(const.taskNo,-1)
        }
        /*if(intent.hasExtra(const.members_list))
        {
            MemberList=intent.getParcelableArrayListExtra(const.members_list)!!
            Log.i("Board member",  MemberList.toString())
            Log.i("Board member2", "nothing ")
        }*/
    }
    // setting up the action bar
    fun setActionBar()
    {
        setSupportActionBar(card_details_act)
        val actionbar=supportActionBar
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionbar.title= board.taskslist[taskPost].cardlist[cardPost].title
        }
        card_details_act.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    /*override fun onBackPressed() {
        if(needUpdate){
            Activity.RESULT_OK
        }
        super.onBackPressed()
    }*/

    fun deleteDialog()
    {
        val dialog=AlertDialog.Builder(this)
        dialog.setTitle("Delete the Card")
        dialog.setMessage("Are your Sure that you want to delete the card")
        dialog.setNegativeButton("No"){

            dialog,wich->
            dialog.dismiss()
            Toast.makeText(this,"Hmm!You changed your mind",Toast.LENGTH_SHORT).show()

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
        val card:Card=board.taskslist[taskPost].cardlist[cardPost]
        val cardlist:ArrayList<Card> =board.taskslist[taskPost].cardlist
        cardlist.remove(card)

        val tasklist =board.taskslist
        tasklist.removeAt(board.taskslist.size-1)
        tasklist[taskPost].cardlist=cardlist
        firestoreClass().boardTasklistupdate(this,board)




    }

    fun addCardUpdateSuccess()
    {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateData()
    {

        val name=et_name_card_details.text.toString()
        if(name.isNotEmpty())
        {
            val newCard:Card=Card(name,
                board.taskslist[taskPost].cardlist[cardPost].createdby,
                board.taskslist[taskPost].cardlist[cardPost].assignedto
                        ,mslectedColor,mslecteddate)
            board.taskslist[taskPost].cardlist[cardPost]=newCard
            val tasklist=board.taskslist
            tasklist.removeAt(tasklist.size-1)

            firestoreClass().boardTasklistupdate(this,board)
        }
        else{
            Toast.makeText(this,"Hey!Card name field is empty",Toast.LENGTH_SHORT).show()
        }

    }

    private fun colorList():ArrayList<String>
    {
        val list:ArrayList<String> = ArrayList()
        list.add("#FF4949")
        list.add("#3A3845")
        list.add("#FF000000")
        list.add("#FF03DAC5")
        list.add("#3EC70B")
        list.add("#826F66")

        return list
    }

    private fun colordialog()
    {
        val colorList:ArrayList<String> = colorList()
        val dialog=object : ColorDailogClass(
            this,
            colorList,
            "Select Color",
            mslectedColor,


        ){
            override fun onItemSelected(color: String) {
                // by overriding we get the color and we will use it
                mslectedColor=color
                setcolor()
            }

        }
        dialog.show()
    }

    private fun showMembersDialog()
    {
        /** here we will be having list of all members are assigned to the card
         * we will take the list and make users selected options true or false on the basis of it
         */
        val cardAssignedMembersList =
            board.taskslist[taskPost].cardlist[cardPost].assignedto

        if (cardAssignedMembersList.size > 0) {
            // Here we got the details of assigned members list from the global members list which is passed from the Task List screen.
            for (i in MembersDataList.indices) {
                for (j in cardAssignedMembersList)
                //why we are comparing the ids because cards will be having the ids of the members
                {
                    if (MembersDataList[i].id == j) {
                        MembersDataList[i].slected=true
                    }
                }
            }
        } else {
            for (i in MembersDataList.indices) {
                MembersDataList[i].slected = false
            }
        }
        val dialog=object:MembersDialogClass(
            this@CardDetailsActivity,
            MembersDataList
        ){
            override fun data( data: Users, Options: String) {
                if(Options==const.selectd_member)
                {
                    if(! board.taskslist[taskPost].cardlist[cardPost].assignedto.contains(data.id))
                    {
                        board.taskslist[taskPost].cardlist[cardPost].assignedto.add(data.id)
                    }
                }
                else
                {
                    board.taskslist[taskPost].cardlist[cardPost].assignedto.remove(data.id)
                    for( i in MembersDataList.indices)
                    {
                        if(MembersDataList[i].id==data.id)
                        {
                            MembersDataList[i].slected=false
                        }
                    }
                }
                selectedMembersAdpterSetup()

            }


        }
                dialog.show()
    }

    private fun setcolor()
    {
        tv_select_label_color.text=""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mslectedColor))
    }

    /** function to set up selectd members */

    private fun selectedMembersAdpterSetup()
    {
        val cardAssignedMembersList =
            board.taskslist[taskPost].cardlist[cardPost].assignedto

        val selectdMember:ArrayList<Selected> =ArrayList()

        for (i in MembersDataList.indices) {
            for (j in cardAssignedMembersList)
            //why we are comparing the ids because cards will be having the ids of the members
            {
                if (MembersDataList[i].id == j) {
                    val newmember=Selected(
                        MembersDataList[i].id,
                        MembersDataList[i].image
                    )
                    selectdMember.add(newmember)
                }
            }
        }

        if(selectdMember.size>0)
        {
            val newmember=Selected(
                "",
                ""
            )
            selectdMember.add(newmember)
            tv_select_members.visibility= View.GONE
            rv_select_members.visibility=View.VISIBLE

            rv_select_members.layoutManager=GridLayoutManager(this,6)
            val adpater=SelectedMembersAdapter(this,selectdMember)
            rv_select_members.adapter=adpater
            adpater.setOnClickListener(
                object:SelectedMembersAdapter.OnClickListener{
                    override fun onClick() {
                        showMembersDialog()
                    }
                }
            )
        }
        else{
            tv_select_members.visibility= View.VISIBLE
            rv_select_members.visibility=View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun DatePicker()
    {
        val caln=Calendar.getInstance()
        val year=caln.get(Calendar.YEAR)
        val month=caln.get(Calendar.MONTH)
        val day=caln.get(Calendar.DAY_OF_MONTH)

        val datePicker=DatePickerDialog(this,DatePickerDialog.OnDateSetListener{
            view,Year,monthofyear,dayofyear->
            val Day=if(dayofyear<10) "0$dayofyear" else "$dayofyear"
            val Month= if((monthofyear+1 )<10) "0${monthofyear+1}" else "0${monthofyear+1}"
            val years="$Year"

            val  selectedate="$Day/$Month/$years"

            tv_date_selcted.text=selectedate
            //val sdf=SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            //val date=sdf.parse(selectedate)
            //val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

            // The formatter will parse the selected date in to Date object
            // so we can simply get date in to milliseconds.
            //val theDate = sdf.parse(selectedate)

            /** Here we have get the time in milliSeconds from Date object
             */

            /** Here we have get the time in milliSeconds from Date object
             */
            mslecteddate = selectedate
        }
        ,year,month,day)
        datePicker.show()
    }


}






