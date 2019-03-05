package com.wk.unichat.Adapt

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wk.unichat.Channels.Msg
import com.wk.unichat.R

import com.wk.unichat.WebRequests.UserData
import kotlinx.android.synthetic.main.content_main.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MsgAdapt(val context: Context, val messages: ArrayList<Msg>) : RecyclerView.Adapter<MsgAdapt.ViewHolder>() { //TODO: Msg?


    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0?. bindMessage(context, messages[p1])
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.msg_list_view, p0, false) //TODO: p0?
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView?.findViewById<ImageView>(R.id.msgUserImage)
        val timeStamp = itemView?.findViewById<TextView>(R.id.msgTime)
        val userName = itemView?.findViewById<TextView>(R.id.msgUserName)
        val messageBody = itemView?.findViewById<TextView>(R.id.msgBodyLabel)


        fun bindMessage(context: Context, message: Msg) {
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userName?.text = message.userName
            timeStamp?.text = dateFormatter(message.timeStamp)
            messageBody?.text = message.msg
        }

        // fun name(argumentName: type) : return
        fun dateFormatter(formatedDate: String) : String {
            // SimpleDateFormat - class that allows us to parse between date->text and text->date
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            var date = Date()
            try{
                date = format.parse(formatedDate) // Converting of our argument
            } catch(e: ParseException) {
                Log.d("PARSE", "Parse error")
            }

            val returnedDate = SimpleDateFormat("E, h:mm a", Locale.getDefault())
            return returnedDate.format(date)
        }
    }
}