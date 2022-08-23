package com.example.firebase.activity.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.activity.models.Board
import com.example.firebase.activity.models.Card
import kotlinx.android.synthetic.main.board_item_layout.view.*
import kotlinx.android.synthetic.main.card_list.view.*

open class CardAdapter(
    private val context: Context,
    private val list: ArrayList<Card>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.
        from(context).
        inflate(R.layout.card_list,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data=list[position]

        if(holder is MyViewHolder)
        {

            if(data.mslectedColor.isNotEmpty())
            {
                holder.itemView.visibility=View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(data.mslectedColor))

            }
            else{
                holder.itemView.view_label_color.visibility=View.GONE
            }
            holder.itemView.tv_card_name.text=data.title
        }


        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}