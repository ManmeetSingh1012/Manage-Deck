package com.example.firebase.activity.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.R
import com.example.firebase.activity.Adapter.MembersAdapter
import com.example.firebase.activity.models.Users
import kotlinx.android.synthetic.main.member_card_details.view.*
import java.text.FieldPosition

abstract class MembersDialogClass (
    context: Context,
    private val list:ArrayList<Users>
        ):Dialog(context)
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val view=LayoutInflater.from(context).inflate(R.layout.member_card_details,null)
        SetupAdapter(view)
        setContentView(view)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    private fun SetupAdapter(view: View)
    {
        view.rv_member_card.layoutManager=LinearLayoutManager(context)
        val adpter=MembersAdapter(context,list)
        view.rv_member_card.adapter=adpter
        adpter.onItemClickListener=object :MembersAdapter.OnItemClickListener{
            override fun onclick(position: Int, data: Users, options: String) {
                dismiss()
                data(data,options)
            }

        }

    }

    protected abstract fun data(data:Users,Options:String)

}