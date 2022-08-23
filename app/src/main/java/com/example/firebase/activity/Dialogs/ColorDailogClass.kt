package com.example.firebase.activity.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.R
import com.example.firebase.activity.Adapter.ColorDialogAdpater
import kotlinx.android.synthetic.main.color_dialog.view.*

/**
 * Abstract class cant have objects
 * this class to create member func so that we can override them
 * these are default open class
 * but for abstract we have to explicitly mention it
 * This Class is used for the displaying the color adapter
 */
abstract class ColorDailogClass(
    context: Context,
    private val list:ArrayList<String>,
    private val mselectedColor:String="",
    private val title:String=""
):Dialog(context)
/**here we are inherting from the dialog class thats why it will display as a dialog*/{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view=LayoutInflater.from(context).inflate(R.layout.color_dialog,null)
        setupAdater(view)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    // for setting up the adpater
    fun setupAdater(view: View)
    {
        view.rv_clr_dig.layoutManager=LinearLayoutManager(context)
        val adapter=ColorDialogAdpater(context,list,mselectedColor)
        view.rv_clr_dig.adapter=adapter
        adapter.onItemClickListener=object :ColorDialogAdpater.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                // getting the selected color
                onItemSelected(color)

            }

        }
    }

    // by overiding whe can get color and we will put that color in mslected color
    protected abstract fun onItemSelected(color: String)
}