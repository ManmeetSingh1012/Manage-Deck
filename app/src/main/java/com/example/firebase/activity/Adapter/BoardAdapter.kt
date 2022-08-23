package com.example.firebase.activity.Adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebase.activity.models.Board
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase.R
import kotlinx.android.synthetic.main.activity_profile_acty.*
import kotlinx.android.synthetic.main.board_item_layout.view.*


open class BoardAdapter(
    private val context: Context, private val list: ArrayList<Board>):RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var onClicklistner: OnClicklistner?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.board_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data=list[position]

        if(holder is MyViewHolder)
        {
            Glide
                .with(context)
                .load(data.image)
                .fitCenter()
                .placeholder(R.color.lightgrey)
                .into(holder.itemView.board_image_ll);

            holder.itemView.board_name_ll.text=data.boardname
            holder.itemView.created_by_ll.text="Created by:${data.createdby}"
        }
        holder.itemView.setOnClickListener {
            if(onClicklistner!=null)
            {
                onClicklistner!!.onClick(position,data)
            }
        }
    }
    interface OnClicklistner{

        fun onClick(position: Int,model:Board)
    }

    fun setonclicklistner(onClicklistner: OnClicklistner)
    {
        this.onClicklistner=onClicklistner
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}