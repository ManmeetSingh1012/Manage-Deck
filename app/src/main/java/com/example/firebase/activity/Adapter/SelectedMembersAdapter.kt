package com.example.firebase.activity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.activity.models.Selected
import kotlinx.android.synthetic.main.card_list.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.selelcted_members_display.view.*

open class SelectedMembersAdapter(
    val context:Context,
    val list:ArrayList<Selected>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListner:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewholders(
            LayoutInflater.from(context).inflate(R.layout.selelcted_members_display,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data=list[position]
        if(holder is MyViewholders){
            if (list.size-1==position)
            {
                holder.itemView.image_members.visibility=View.GONE
                holder.itemView.Add_card_members.visibility=View.VISIBLE
            }
            else{
                holder.itemView.image_members.visibility=View.VISIBLE
                holder.itemView.Add_card_members.visibility=View.GONE

                Glide
                    .with(context)
                    .load(data.image)
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(holder.itemView.image_members);
            }
           holder.itemView.setOnClickListener{
               if(onClickListner!=null)
               {
                   onClickListner!!.onClick()
               }
           }

        }
    }
    interface OnClickListener{
        fun onClick()
    }

    fun setOnClickListener(onClickListner:OnClickListener)
    {
        this.onClickListner=onClickListner
    }

    override fun getItemCount():Int{
        return list.size
    }

    class MyViewholders(view:View):RecyclerView.ViewHolder(view)
}