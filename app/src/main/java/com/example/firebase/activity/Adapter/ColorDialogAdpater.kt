package com.example.firebase.activity.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.R
import kotlinx.android.synthetic.main.color_label.view.*

class ColorDialogAdpater(
   private val  context:Context,
    private val list:ArrayList<String>,
   private val mselectedColor:String
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.color_label,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item=list[position]
        if(holder is MyViewHolder)
        {
            // it will set the color
            holder.itemView.colorBar.setBackgroundColor(Color.parseColor(item))
            // it will display the tick if the color is same as selected color
            if(item!=mselectedColor)
            {
                holder.itemView.color_ok_view.visibility=View.GONE
            }
            else
            {
                holder.itemView.color_ok_view.visibility=View.VISIBLE
            }
            // when color is selected the color is passed to color dialog
            holder.itemView.setOnClickListener {

                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)

    interface OnItemClickListener {

        fun onClick(position: Int, color: String)
    }
}