package com.example.firebase.activity.activites

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.firebase.R
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.Board
import com.example.firebase.activity.models.const
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_board.floating_btn_board
import kotlinx.android.synthetic.main.app_barmain.tool_bar_board

class BoardActivity : BaseActivity() {


    //every time put const val in companion obj
    companion object{
        private const val gallery_board:Int=11

    }
    var photouri_board:Uri?=null
    var musername:String=""
    var FirestroreImageuri:String=""
    var listassignedto:ArrayList<String> =ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // here we are recieving the user name from the main activity
        if(intent.hasExtra(const.username))
        {
            musername= intent.getStringExtra(const.username).toString()
        }
        bar()
        floating_btn_board.setOnClickListener {
            Pickgallery()
        }
        board_create_btn.setOnClickListener {
            Create()
        }

    }

    private fun Create()
    {
        val brdname=board_name.text.toString()

        listassignedto.add(getcurrentuserid())



        if(brdname.isNotEmpty())
        {
            try{
                val data=Board(boardname = brdname, createdby = musername, image = FirestroreImageuri, assignedto =listassignedto )
                firestoreClass().registerduserBoards(this,data)

            }catch ( e:Exception){
                Log.e("err","something went wrong")
                Log.e("error",e.message.toString())
            }
        }
        else{
            Toast.makeText(this,"Board Name is nessary to give ",Toast.LENGTH_SHORT).show()
        }




    }




    // another method to add to get navigation bar
    private fun bar()
    {
        setSupportActionBar(tool_bar_board)
        val actionbar=supportActionBar
        if(actionbar!=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            tool_bar_board.setNavigationOnClickListener {
                onBackPressed()
            }

        }

    }


    private fun Pickgallery()
    {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    //Toast.makeText(this@Add_Places,"Permission Granted",Toast.LENGTH_SHORT).show()
                    val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, gallery_board)

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@BoardActivity,"Permission Denied", Toast.LENGTH_SHORT).show()
                }

                // this will show why we need permission
                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    dialog2Gallery()
                }

            }).onSameThread().check()
    }

    // this dialog will shown when permssion denid for the gallery
    private fun dialog2Gallery() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Permission Required to acess the Gallery ,Go to settings and allow us to use Gallery")
        dialog.setPositiveButton("Cancel") { dialgo, _ ->
            try {
                // this will take us directly to settings page

                //this will be intent or allow us to go over there
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                //  the path to get to specific location of the app in settings
                val uri = Uri.fromParts("package", packageName, null)

                //passing the path
                intent.data = uri
                startActivity(intent)

            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun BoardRegisteredSucc()
    {
        //dismissProgressbar()
        /** here we are setting the result ok for the board so we can update in the main activity*/
        setResult(RESULT_OK)
        finish()
    }

    // image setter
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(resultCode == Activity.RESULT_OK  && requestCode== gallery_board)
        {
            if(data!=null)
            {
                photouri_board= data.data!!



                try {
                    image_board.setImageURI(photouri_board)

                }catch (e:Exception)
                {
                    e.printStackTrace()
                    Toast.makeText(this,"something went wrong", Toast.LENGTH_SHORT).show()
                }
                uploadtostorage()
            }
        }
    }

    private fun uploadtostorage()
    {
        showprogressbar()
        if(photouri_board!=null)
        {
            val ref: StorageReference = FirebaseStorage.getInstance().reference.child("User_Name"+System.currentTimeMillis()+
                    "."+getfileextension(photouri_board))

            ref.putFile(photouri_board!!).addOnSuccessListener {


                    it -> Log.i("image uri storage",
                it.metadata!!.reference!!.downloadUrl.toString())

                dismissProgressbar()

                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->

                    FirestroreImageuri=uri.toString()
                    Log.i("image uri stor 2",FirestroreImageuri)
                    //updateuserprofiledataimage()
                }.addOnFailureListener {

                    Toast.makeText(this,it.message,
                        Toast.LENGTH_SHORT).show()
                    dismissProgressbar()
                    Log.i("error occ","error ")
                }
            }.addOnFailureListener {
                dismissProgressbar()
                Log.i("error occ2","error ")
            }


        }
    }
    private fun getfileextension(uri:Uri?):String{
        var type: String? = null
        type = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
        Log.i("exten","$type")
        return type!!
    }


}