package com.example.firebase.activity.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.activity.models.Users
import com.example.firebase.activity.models.const
import kotlinx.android.synthetic.main.members_list.view.*

open class MembersAdapter(
    private val context: Context,
    private val list:ArrayList<Users>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyviewHolder(LayoutInflater.from(context).inflate(R.layout.members_list,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data=list[position]

        Log.i("adpater_mber","members")
        if(holder is MyviewHolder)
        {
            holder.itemView.tv_member_name.text=data.name
            holder.itemView.tv_member_email.text=data.email
            Glide
                .with(context)
                .load(data.image)
                .fitCenter()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(holder.itemView.iv_member_image);


            if(data.slected)
            {
                holder.itemView.selected_member.visibility=View.VISIBLE
            }
            else{
                holder.itemView.selected_member.visibility=View.GONE
            }
            holder.itemView.setOnClickListener {

                    if(onItemClickListener!=null)
                    {
                        if(data.slected)
                        {
                            onItemClickListener!!.onclick(position,data,const.Nonselected_meber)
                        }
                        else
                        {
                            onItemClickListener!!.onclick(position,data,const.selectd_member)
                        }
                    }


            }



        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {

        fun onclick(position: Int, data:Users,options:String)
    }


    class MyviewHolder(view:View):RecyclerView.ViewHolder(view)
}