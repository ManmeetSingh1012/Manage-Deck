package com.example.firebase.activity.FCM

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.firebase.R
import com.example.firebase.activity.Firestore.firestoreClass
import com.example.firebase.activity.activites.MainActivity
import com.example.firebase.activity.activites.SignInActivity
import com.example.firebase.activity.models.const
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

open class MyFirebaseMessagingService:FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG,"${message.from}")
        message.data.isNotEmpty().let {
            Log.d(TAG,"Message data Payload:${message.data}")
            val title=message.data[const.FCM_KEY_TITLE]!!
            val Message=message.data[const.FCM_KEY_MESSAGE]!!

            sendNotifications(title,Message)
        }

        message.notification?.let{
            Log.d(TAG,"Message Notification Body:${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG,"Refreshed taken:$token")
        sendRegistrationToserver(token)
    }

    private fun sendRegistrationToserver(token: String?)
    {
        val sharedPreferences =
            this.getSharedPreferences(const.Project_Md_sharedPref, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(const.fcmToken, token)
        editor.apply()
    }

    private fun sendNotifications(title:String,message:String)
    {
        val intent=if(firestoreClass().getCurrUserId().isNotEmpty() )
        {
            Intent(this,MainActivity::class.java)
        }
        else{
            Intent(this,SignInActivity::class.java)
        }
        // this flags will not allow overlaping of acitivtes it will clear the task
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId=this.getString(R.string.default_notification)
        val defaultSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder=NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManger=getSystemService(
            Context.NOTIFICATION_SERVICE
         ) as NotificationManager
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                val channel = NotificationChannel(channelId,"Channel Md title",NotificationManager.IMPORTANCE_DEFAULT)
                notificationManger.createNotificationChannel(channel)
            }
        notificationManger.notify(0,notificationBuilder.build())

    }

    companion object{
        private const val TAG="MymessageService"
    }
}