package com.example.firebase.activity.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.R
import com.example.firebase.activity.activites.TaskActivityList
import com.example.firebase.activity.models.Task
import kotlinx.android.synthetic.main.task_list.view.*

open class TaskAdapter(

    private val context: Context,private val list:ArrayList<Task>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view=LayoutInflater.from(context).inflate(R.layout.task_list,parent,false)



       /* val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = view.width*0.7.toInt()
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.setLayoutParams(params)*/

        return myViewholder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position:Int) {
        val model=list[position]

        if(holder is myViewholder)
        {

            if(position==list.size-1)
            {
                holder.itemView.ll_task_item_all.visibility=View.GONE
                holder.itemView.task_list_add_btn.visibility=View.VISIBLE
            }else{
                holder.itemView.ll_task_item_all.visibility=View.VISIBLE
                holder.itemView.task_list_add_btn.visibility=View.GONE
                holder.itemView.tv_task_list_title.text=model.title
            }

            // for writting  name
            holder.itemView.task_list_add_btn.setOnClickListener {
                holder.itemView.cv_add_task_list_name.visibility=View.VISIBLE
                holder.itemView.task_list_add_btn.visibility=View.GONE
            }

            // for cancelling
            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.cv_add_task_list_name.visibility=View.GONE
                holder.itemView.task_list_add_btn.visibility=View.VISIBLE
            }

            // for done or adding the name
            holder.itemView.ib_done_list_name.setOnClickListener {

                holder.itemView.cv_add_task_list_name.visibility = View.GONE
                holder.itemView.task_list_add_btn.visibility = View.VISIBLE
                // checking the name
                val tasklistname = holder.itemView.et_task_list_name.text.toString()
                if (tasklistname.isNotEmpty()) {

                    Log.i("task1", "every thing is fine")
                    // passing the name to the context so that it can update it
                    //(context as? TaskActivityList)?.createTaskList(tasklistname!!)
                    if (context is TaskActivityList) {
                        Log.i("task2", "every thing is fine")
                        context.createTaskList(tasklistname)
                    }
                    Log.i("task1", "every thing is fine")


                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

                // for deleting the name
                holder.itemView.ib_delete_list.setOnClickListener{
                    if( context is TaskActivityList){
                        context.deletelistname(model)
                    }

                }

                // for edtiting the name
                holder.itemView.ib_edit_list_name.setOnClickListener {
                    holder.itemView.et_edit_task_list_name.setText(model.title)

                    holder.itemView.ll_title_view.visibility=View.GONE
                    holder.itemView.cv_edit_task_list_name.visibility=View.VISIBLE

                }

            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility=View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility=View.GONE
            }

            holder.itemView.ib_done_edit_list_name.setOnClickListener{
                val listname=holder.itemView.et_edit_task_list_name.text.toString()

                holder.itemView.ll_title_view.visibility=View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility=View.GONE

                if (listname.isNotEmpty()) {

                    // passing the name to the context so that it can update it
                    //(context as? TaskActivityList)?.createTaskList(tasklistname!!

                    if (context is TaskActivityList) {

                        context.editTaskListTitle(listname,position,model)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.tv_add_card.setOnClickListener {
                holder.itemView.cv_add_card.visibility=View.VISIBLE
            }

            /** for card */
            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.cv_add_card.visibility=View.GONE
            }

            holder.itemView.ib_done_card_name.setOnClickListener {
                val listname=holder.itemView.et_card_name.text.toString()

                holder.itemView.cv_add_card.visibility=View.GONE

                if (listname.isNotEmpty()) {

                    // passing the name to the context so that it can update it
                    //(context as? TaskActivityList)?.createTaskList(tasklistname!!

                    if (context is TaskActivityList) {

                        context. addcards(listname,position)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.rv_card_list.layoutManager= LinearLayoutManager(context)
            //holder.itemView.rv_card_list.setHasFixedSize(true)
            val adapter=CardAdapter(context,model.cardlist)
            holder.itemView.rv_card_list.adapter=adapter

            adapter.setOnClickListener(object :
                CardAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {

                    if (context is TaskActivityList) {
                        context.cardDetails(position, cardPosition)
                    }
                }
            })


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class myViewholder(view:View):RecyclerView.ViewHolder(view)



    // this will set the margins from left right and top and bottom
    class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                left = spaceSize
                right = spaceSize

            }
        }
    }




}



