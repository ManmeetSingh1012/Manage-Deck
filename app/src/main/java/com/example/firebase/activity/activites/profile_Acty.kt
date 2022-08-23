package com.example.firebase.activity.activites

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.models.Users
import com.example.firebase.activity.models.const
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_profile_acty.*
import kotlinx.android.synthetic.main.edti_info_file.view.*


class profile_Acty : BaseActivity() {
    companion object{
        private const val gallery=2
    }
    lateinit var msharedpref: SharedPreferences
    private var photouri:Uri?=null
    private var mfirestore=FirebaseFirestore.getInstance()
    private var FirestroreImageuri:String=""
    private lateinit var userDetails:Users


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_acty)

        msharedpref=getSharedPreferences(const.Gender, Context.MODE_PRIVATE)



        firestoreClass().Dataprovider(this)
        // click listner to pick image form gallery
        floating_btn_board.setOnClickListener {
            Pickgallery()

        }

        edit_btn_name.setOnClickListener{


            editDialog(1)
        }
        phone_pro_edit.setOnClickListener{
            editDialog(2)
        }



    }


    // this will display the data
    fun displayTheData(data: Users)
    {
        userDetails=data
        name_pro.setText(data.name)
        email_prof.setText(data.email)
        phone_pro.setText(data.phone.toString())
        Glide
            .with(this)
            .load(data.image)
            .fitCenter()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(image_profile);
    }





    // image picker
    private fun Pickgallery()
    {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    //Toast.makeText(this@Add_Places,"Permission Granted",Toast.LENGTH_SHORT).show()
                    val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, gallery)

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@profile_Acty,"Permission Denied", Toast.LENGTH_SHORT).show()
                }

                // this will show why we need permission
                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    dialog2Gallery()
                }

            }).onSameThread().check()
    }


    // image setter
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(resultCode == Activity.RESULT_OK  && requestCode== gallery)
        {
            if(data!=null)
            {
                photouri=data.data
                //getfileextension(photouri)
                uploadtostorage()

                try {
                    image_profile.setImageURI(photouri)
                    /*mfirestore.collection(const.USER).document(firestoreClass().getCurrUserId())
                        .set(userinfo, SetOptions.merge()).addOnSuccessListener {
                            Toast.makeText(this,"Profile Photo updated",Toast.LENGTH_SHORT).show()
                        }*/
                }catch (e:Exception)
                {
                    e.printStackTrace()
                    Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    // this dialog will shown when permssion denid for the gallery
    private fun dialog2Gallery() {
        val dialog=AlertDialog.Builder(this)
        dialog.setMessage("Permission Required to acess the Gallery ,Go to settings and allow us to use Gallery")
        dialog.setPositiveButton("Cancel"){
                dialgo,_->
            try{
                // this will take us directly to settings page

                //this will be intent or allow us to go over there
                val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                //  the path to get to specific location of the app in settings
                val uri= Uri.fromParts("package",packageName,null)

                //passing the path
                intent.data=uri
                startActivity(intent)

            }catch (e: ActivityNotFoundException)
            {
                e.printStackTrace()
            }
        }

        dialog.setNegativeButton("Cancel"){ dialog,_->dialog.dismiss()}
        dialog.create().show()
    }

    // this for edit items
    @SuppressLint("ClickableViewAccessibility")
    private fun editDialog(no:Int)
    {


        /**
         * The LayoutInflater class is used to instantiate the contents of layout XML files into their corresponding View objects.
         * In other words, it takes an XML file as input and builds the View objects from it.
         * if you want a view from a xml use inflater
         */


        val alertDialog = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.edti_info_file, null)

        alertDialog.setCancelable(false)
        alertDialog.setView(dialogView)

        if(no==2) dialogView.title_add_members.text="Phone No"

        val btOk = dialogView.findViewById(R.id.save_edit_btn) as Button
        val btCancel = dialogView.findViewById(R.id.cancel_edit_btn_mem) as Button

        btOk.setOnClickListener(View.OnClickListener {
            if(no ==1) {
                if (savedata(dialogView)) {
                    alertDialog.dismiss()
                }
            }
            else{
                if (savedataPhone(dialogView)) {
                    alertDialog.dismiss()
                }
            }

        })

        btCancel.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Edit dialog closed..!!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        })

        alertDialog.show()






    }

    private fun savedata(view: View):Boolean
    {

        val editText=view.findViewById<EditText>(R.id.data_member_email_add)





        if(editText.text.isNotEmpty() && editText.text.toString() != userDetails.name)
        {
            showprogressbar()
            name_pro.text=editText.text
            val userhasmap=HashMap<String,Any>()
            userhasmap[const.Name]=editText.text.toString()
            firestoreClass().udpateUserProfile(this,userhasmap)
            return true
        }

        else{
            Toast.makeText(this,"Name Field is empty",Toast.LENGTH_SHORT).show()
        }
        return true

    }

    private fun savedataPhone(view: View):Boolean
    {

        val editText=view.findViewById<EditText>(R.id.data_member_email_add)





        if(editText.text.isNotEmpty() && editText.text.toString().toLong() != userDetails.phone)
        {
            showprogressbar()
            phone_pro.text=editText.text
            val userhasmap=HashMap<String,Any>()
            userhasmap[const.Mobile]=editText.text.toString().toLong()
            firestoreClass().udpateUserProfile(this,userhasmap)
            return true
        }

        else{
            Toast.makeText(this,"Name Field is empty",Toast.LENGTH_SHORT).show()
        }
        return true

    }



    private fun uploadtostorage()
    {
        showprogressbar()
        if(photouri!=null)
        {
            val ref:StorageReference=FirebaseStorage.getInstance().reference.child("User_Name"+System.currentTimeMillis()+
            "."+getfileextension(photouri))

            ref.putFile(photouri!!).addOnSuccessListener {


                it -> Log.i("image uri storage",
                it.metadata!!.reference!!.downloadUrl.toString())

                dismissProgressbar()

                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("image uri stor 2",uri.toString())
                    FirestroreImageuri=uri.toString()
                    updateuserprofiledataimage()
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

    // to update data in drawer
    fun onsucees()
    {
        dismissProgressbar()
        setResult(Activity.RESULT_OK)
        dialogSnackbar("Profile Updated Successfully")
        startActivity(Intent(this,MainActivity::class.java))
    }

    fun updateuserprofiledataimage()
    {
        val userhasmap=HashMap<String,Any>()
        if(FirestroreImageuri.isNotEmpty() && FirestroreImageuri != userDetails.image)
        {
            userhasmap[const.image]=FirestroreImageuri
        }
        firestoreClass().udpateUserProfile(this,userhasmap)
    }




}