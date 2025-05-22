package com.example.chatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var imageViewCircleProfile: CircleImageView
    private lateinit var editTextUserNameProfile: TextInputEditText
    private lateinit var buttonUpdate: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private var imageUri: Uri? = null
    private var imageControl = false
    private var image: String? = null

    // Registering for activity result (replacement for onActivityResult)
    private val imageChooserLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Picasso.get().load(imageUri).into(imageViewCircleProfile)
            imageControl = true
        } else {
            imageControl = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageViewCircleProfile = findViewById(R.id.imageViewCircleProfile)
        editTextUserNameProfile = findViewById(R.id.editTextUserNameProfile)
        buttonUpdate = findViewById(R.id.buttonUpdate)

        database = FirebaseDatabase.getInstance()
        reference = database.reference
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        getUserInfo()

        imageViewCircleProfile.setOnClickListener {
            imageChooser()
        }

        buttonUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val userName = editTextUserNameProfile.text.toString()
        firebaseUser?.uid?.let {
            reference.child("Users").child(it).child("userName").setValue(userName)
        }

        if (imageControl) {
            val randomID = UUID.randomUUID()
            val imageName = "images/$randomID.jpg"
            imageUri?.let { uri ->
                storageReference.child(imageName).putFile(uri)
                    .addOnSuccessListener {
                        val myStorageRef = firebaseStorage.getReference(imageName)
                        myStorageRef.downloadUrl.addOnSuccessListener { uriDownload ->
                            val filePath = uriDownload.toString()
                            auth.uid?.let { uid ->
                                reference.child("Users").child(uid).child("image").setValue(filePath)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Write to database is successful.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Write to database is not successful.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
            }
        } else {
            auth.uid?.let { uid ->
                reference.child("Users").child(uid).child("image").setValue(image)
            }
        }

        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        intent.putExtra("userName", userName)
        startActivity(intent)
        finish()
    }

    private fun getUserInfo() {
        firebaseUser?.uid?.let { uid ->
            reference.child("Users").child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("userName").getValue(String::class.java) ?: ""
                    image = snapshot.child("image").getValue(String::class.java)

                    editTextUserNameProfile.setText(name)

                    if (image == "null" || image.isNullOrEmpty()) {
                        imageViewCircleProfile.setImageResource(R.drawable.account)
                    } else {
                        Picasso.get().load(image).into(imageViewCircleProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle if needed
                }
            })
        }
    }

    private fun imageChooser() {
        imageChooserLauncher.launch("image/*")
    }
}
