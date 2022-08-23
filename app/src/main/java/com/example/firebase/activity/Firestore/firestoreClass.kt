package com.example.firebase.activity.Firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.firebase.activity.activites.*
import com.example.firebase.activity.models.Board
import com.example.firebase.activity.models.Task
import com.example.firebase.activity.models.Users
import com.example.firebase.activity.models.const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class firestoreClass {

    private val mfirestore=FirebaseFirestore.getInstance()
    fun registerduser(activity:Sign_up,userinfo:Users)
    {
        mfirestore.collection(const.USER).document(getCurrUserId())
            .set(userinfo, SetOptions.merge()).addOnSuccessListener {
                activity.UserRegisteredSuccessfully()
            }
    }

    // this will register the data for the boards
    fun registerduserBoards(activity:BoardActivity,board: Board)
    {
        mfirestore.collection(const.Board).document()
            .set(board, SetOptions.merge()).addOnSuccessListener {
                activity.BoardRegisteredSucc()
                activity.dialogSnackbar("Board Created Successfully")
            }.addOnFailureListener {

                Log.e("error board", it.toString())
                activity.dialogSnackbar("Unable to create the Baord")
            }
    }

    // getting board data using the current user id
    fun getboardata(activity: MainActivity)
    {
        mfirestore.collection(const.Board).whereArrayContains(const.Assigned,getCurrUserId())
            .get().addOnSuccessListener {
                document ->
                val boardlist:ArrayList<Board> = ArrayList()
                // here when we are getting the board with this we are also assigininig the document id
                for( i in document.documents)
                {
                    // i.to obj only due to parcable
                    val board =i.toObject(Board::class.java)!!
                    board.documentedId=i.id
                    Log.i("dcid1",board.documentedId)
                    boardlist.add(board)
                }
                activity.DisplayBoads(boardlist)
            }.addOnFailureListener {
                e->
                Log.i("board error","something went wrong")

            }
    }

    // for getting the task insided the board using the board the document id
    fun boardTasklist(activity: TaskActivityList,document:String)
    {
        mfirestore.collection(const.Board).document(document)
            .get().addOnSuccessListener {


                //activity.dismissProgressbar()
                val list: Board =it.toObject(Board::class.java)!!
                list.documentedId=it.id

                Log.i("boardlist","every thing is allright")
                activity.tasklist(list)
            }.addOnFailureListener {
                    e->
                //activity.dismissProgressbar()
                Log.e("board error","something went wrong")

            }

    }

    // for updating the task list taking name from task
    fun boardTasklistupdate(activity: Activity,boards: Board)
    {
        var hashMap=HashMap<String,Any>()
        hashMap[const.taskslist]=boards.taskslist

        mfirestore.collection(const.Board).document(boards.documentedId)


            .update(hashMap).addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                if(activity is TaskActivityList)
                    activity.addUpdateTaskListSuccess()
                else if(activity is CardDetailsActivity)
                    activity.addCardUpdateSuccess()
            }
            .addOnFailureListener { e ->
                if(activity is TaskActivityList)
                    activity.dismissProgressbar()
                else if(activity is CardDetailsActivity)
                    activity.dismissProgressbar()

                Toast.makeText(activity,"Something went wrong ",Toast.LENGTH_SHORT).show()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }

    }






    fun registerduserGoogle(activity:Sign_up,userinfo:Users)
    {
        mfirestore.collection(const.USER).document(getCurrUserId())
            .set(userinfo, SetOptions.merge()).addOnSuccessListener {
                activity.UserRegisteredSuccessfullyGoogle()
            }
    }

    /**
     * here const.USER is the name of the collection
     * document will give the data of current user
     */

    // provide the data
    // here we have seted the bar update value to false so if want we can use it if not then no nedd of this
    fun Dataprovider(activity:Activity,boardupdate:Boolean=false)
    {
        mfirestore.collection(const.USER).document(getCurrUserId())
            .get().addOnSuccessListener {
                // here we have downloaded data of document or current user and converted in the form of Users model class
                val loggedinuser=it.toObject(Users::class.java)

                when(activity)
                {
                    is Sign_up ->{
                        if(loggedinuser!=null)
                        {
                            activity.UserRegisteredSuccessfullyEntry(loggedinuser)
                        }
                    }
                    is SignInActivity -> {
                        if(loggedinuser!=null)
                            activity.UserRegisteredSuccessfully(loggedinuser)
                    }

                    is MainActivity ->{
                        if (loggedinuser != null) {
                            activity.updateNavigationUserData(loggedinuser,boardupdate)
                        }
                    }

                    is profile_Acty->
                    {
                        if (loggedinuser != null) {
                            activity.displayTheData(loggedinuser)

                        }
                    }
                }


            }
    }

    // Data loader for google
    fun DataproviderForGoogle(activity:SignInActivity)
    {
        mfirestore.collection(const.USER).document(getCurrUserId())
            .get().addOnSuccessListener {
                // here we have downloaded data of document or current user and converted in the form of Users model class
                val loggedinuser=it.toObject(Users::class.java)

                if (loggedinuser != null) {
                    activity.UserRegisteredSuccessfully(loggedinuser)
                }


            }
    }

    fun getCurrUserId():String{
        val firebaseauth=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(firebaseauth!=null)
        {
            currentUserId=firebaseauth.uid
        }
        return currentUserId
    }

    // updating the data using Hashmap
    fun udpateUserProfile(activity:Activity,hashMap: HashMap<String,Any>){

        mfirestore.collection(const.USER).document(getCurrUserId())
            .update(hashMap)
            .addOnSuccessListener {
                Log.i("updated","updated sucessfully")
                when(activity)
                {
                    is profile_Acty -> activity.onsucees()
                    is MainActivity -> activity.onTokenSucces()
                }

            }.addOnFailureListener {
                it->
                when(activity)
                {
                    is profile_Acty -> activity.dismissProgressbar()
                    //is MainActivity -> activity.dismissProgressbar()
                }
                Log.i("error","error occurd",it)
                //activity.dialogSnackbar("Something went wrong")
            }
    }

    /** this giving the detials of the members assigned to particular Board*/
    fun membersDetails(activity: Activity,list:ArrayList<String>)
    {
        mfirestore.collection(const.USER)
            .whereIn(const.ID,list).get().addOnSuccessListener {QuerySnapshot->

                    if(activity is Members_Activity)
                        activity.dismissProgressbar()
                    if(activity is CardDetailsActivity)
                        activity.dismissProgressbar()

                val user:ArrayList<Users> = ArrayList()
                for( i in QuerySnapshot.documents)
                {
                    val items=i.toObject(Users::class.java)!!
                    user.add(items)
                }
                Log.i("memeber ok","all is ok")
                if(activity is Members_Activity)
                    activity.setupAdpater(user)
                if(activity is CardDetailsActivity)
                    activity.gettingMemberList(user)

            }.addOnFailureListener {
                    e ->
                if(activity is Members_Activity)
                    activity.dismissProgressbar()
                if(activity is CardDetailsActivity)
                    activity.dismissProgressbar()

                Log.e(
                        "member error",
                    "Error while creating a board.",
                    e
                )
            }

    }
    /** this will give the details of the user using email so that we can show the member in the members activity*/
    fun getMembersDetails(context:Members_Activity, email: String)
    {
        mfirestore.collection(const.USER)
            .whereEqualTo(const.email,email)
            .get()
            .addOnSuccessListener { QuerySnapshot->
                context.dismissProgressbar()
                if(QuerySnapshot.documents.size>0){
                    val usr=QuerySnapshot.documents[0].toObject(Users::class.java)!!
                    context.ReciveUserData(usr)
                }else{
                    context.dialogSnackbar("User Not Found!!")
                }

            }.addOnFailureListener {

                e->
                context.dismissProgressbar()
                Log.e(
                    "member_email_error",
                    "Error while adding a member",
                    e
                )
            }
    }

    /** this will update new added member data in board assigned data*/
    fun updateMemberdata(context: Members_Activity,board: Board,user:Users)
    {
        val list:HashMap<String,Any> = HashMap()
        list[const.Assigned]=board.assignedto
        mfirestore.collection(const.Board)
            .document(board.documentedId)
            .update(list)
            .addOnSuccessListener {
                context.suceesUpdateMember(user)
            }.addOnFailureListener {

                    e->
                context.dismissProgressbar()
                Log.e(
                    "member_update_error",
                    "Error while adding a member",
                    e
                )
            }
    }

    /** to delte the board*/
    fun boardTasklistDelete(activity: TaskActivityList,boards: Board)
    {



        mfirestore.collection(const.Board).document(boards.documentedId)


            .delete().addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList Delete successfully.")

                activity.DeleteSuccess()

            }
            .addOnFailureListener { e ->

                activity.dismissProgressbar()


                Toast.makeText(activity,"Something went wrong ",Toast.LENGTH_SHORT).show()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }

    }
}