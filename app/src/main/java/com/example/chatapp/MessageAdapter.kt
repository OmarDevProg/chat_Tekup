package com.example.chatapp

import com.example.chatapp.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ModelClass

class MessageAdapter(list: MutableList<ModelClass?>, userName: String?) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder?>() {
    var list: MutableList<ModelClass?>
    var userName: String?

    var status: Boolean
    var send: Int
    var receive: Int

    init {
        this.list = list
        this.userName = userName

        status = false
        send = 1
        receive = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View
        if (viewType == send) {
            view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_send, parent, false)
        } else {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card_received, parent, false)
        }

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.textView?.text = list[position]?.message
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView? = null

        init {
            if (status) {
                textView = itemView.findViewById<TextView?>(R.id.textViewSend)
            } else {
                textView = itemView.findViewById<TextView?>(R.id.textViewReceived)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position]?.from == userName) {
            status = true
            return send
        } else {
            status = false
            return receive
        }
    }
}