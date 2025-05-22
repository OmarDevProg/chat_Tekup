package com.example.chatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var imageViewCircle: CircleImageView
    private lateinit var editTextEmailSignup: TextInputEditText
    private lateinit var editTextPasswordSignup: TextInputEditText
    private lateinit var editTextUserNameSignup: TextInputEditText
    private lateinit var buttonRegister: android.widget.Button

    private var imageControl = false
    private var imageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: StorageReference
    private lateinit var firebaseStorage: FirebaseStorage

    // Use the new Activity Result API to select image
    private val imageChooserLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            Picasso.get().load(it).into(imageViewCircle)
            imageControl = true
        } ?: run {
            imageControl = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        imageViewCircle = findViewById(R.id.imageViewCircle)
        editTextEmailSignup = findViewById(R.id.editTextEmailSignup)
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup)
        editTextUserNameSignup = findViewById(R.id.editTextUserNameSignup)
        buttonRegister = findViewById(R.id.buttonRegister)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = FirebaseStorage.getInstance().reference
        firebaseStorage = FirebaseStorage.getInstance()

        imageViewCircle.setOnClickListener {
            imageChooser()
        }

        buttonRegister.setOnClickListener {
            val email = editTextEmailSignup.text.toString()
            val password = editTextPasswordSignup.text.toString()
            val userName = editTextUserNameSignup.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty()) {
                signup(email, password, userName)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun imageChooser() {
        // Using Activity Result API to get image from gallery
        imageChooserLauncher.launch("image/*")
    }

    private fun signup(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.uid ?: return@addOnCompleteListener

                val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                userRef.child("userName").setValue(userName)

                if (imageControl && imageUri != null) {
                    val randomID = UUID.randomUUID()
                    val imageName = "images/$randomID.jpg"
                    val imageRef = reference.child(imageName)

                    imageRef.putFile(imageUri!!)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                userRef.child("image").setValue(uri.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Write to database is successful.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Write to database is not successful.", Toast.LENGTH_SHORT).show()
                                    }
                            }

                        }
                } else {
                    userRef.child("image").setValue("null")
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "There is a problem.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
