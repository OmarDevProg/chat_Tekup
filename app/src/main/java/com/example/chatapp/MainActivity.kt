package com.example.chatapp
import com.example.chatapp.R

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.MyLoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var rv: RecyclerView? = null
    var user: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    var userName: String? = null

    var list: MutableList<String?>? = null
    var adapter: UsersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById<RecyclerView?>(R.id.rv)
        rv!!.setLayoutManager(LinearLayoutManager(this))
        list = ArrayList<String?>()

        auth = FirebaseAuth.getInstance()
        user = auth!!.getCurrentUser()
        database = FirebaseDatabase.getInstance()
        reference = database!!.getReference()

        reference!!.child("Users").child(user!!.getUid()).child("userName")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userName = snapshot.getValue().toString()


                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    val users: Unit
        get() {
            reference!!.child("Users").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val key = snapshot.getKey()

                    if (key != user!!.getUid()) {
                        list!!.add(key)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.chat_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        if (item.getItemId() == R.id.action_signout) {
            auth!!.signOut()
            startActivity(Intent(this@MainActivity, MyLoginActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}