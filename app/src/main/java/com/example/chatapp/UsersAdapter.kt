package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(
    private val userList: List<String>,
    private val userName: String,
    private val mContext: Context
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_users_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = userList[position]

        reference.child("Users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val otherName = snapshot.child("userName").getValue(String::class.java) ?: "Unknown"
                val imageURL = snapshot.child("image").getValue(String::class.java) ?: "null"

                holder.textViewUsers.text = otherName

                if (imageURL == "null") {
                    holder.imageViewUsers.setImageResource(R.drawable.account)
                } else {
                    // Uncomment the line below to load image with Picasso once you fix your Firebase Storage issues
                    // Picasso.get().load(imageURL).into(holder.imageViewUsers)
                    holder.imageViewUsers.setImageResource(R.drawable.account) // placeholder while debugging
                }

                holder.cardView.setOnClickListener {
                    val intent = Intent(mContext, MyChatActivity::class.java).apply {
                        putExtra("userName", userName)
                        putExtra("otherName", otherName)
                    }
                    mContext.startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    override fun getItemCount(): Int = userList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUsers: TextView = itemView.findViewById(R.id.textViewUsers)
        val imageViewUsers: CircleImageView = itemView.findViewById(R.id.imageViewUsers)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
