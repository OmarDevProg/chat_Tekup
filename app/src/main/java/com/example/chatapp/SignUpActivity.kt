package com.example.chatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class SignUpActivity : AppCompatActivity() {


    private lateinit var imageViewCircle: CircleImageView
    private lateinit var editTextEmailSignup: TextInputEditText
    private lateinit var editTextPasswordSignup: TextInputEditText
    private lateinit var editTextUserNameSignup: TextInputEditText
    private lateinit var buttonRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageControl = false
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imageViewCircle = findViewById(R.id.imageViewCircle)
        editTextEmailSignup = findViewById(R.id.editTextEmailSignup)
        editTextPasswordSignup = findViewById(R.id.EditTextPasswordSignup)
        editTextUserNameSignup = findViewById(R.id.EditTextUserNameSignup)
        buttonRegister = findViewById(R.id.buttonRegister)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        // Set up image selection
        imageViewCircle.setOnClickListener {
            imageChooser()
        }

        // Set up register button
        buttonRegister.setOnClickListener {
            val email = editTextEmailSignup.text.toString()
            val password = editTextPasswordSignup.text.toString()
            val userName = editTextUserNameSignup.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty()) {
                signup(email, password, userName)
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri).into(imageViewCircle)
            imageControl = true
        } else {
            imageControl = false
        }
    }

    private fun signup(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.uid ?: return@addOnCompleteListener

                // Save username
                database.child("Users").child(userId).child("userName").setValue(userName)

                if (imageControl) {
                    val randomID = com.android.identity.util.UUID.randomUUID().toString()
                    val imageName = "images/$randomID.jpg"
                    storageReference.child(imageName).putFile(imageUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
                                database.child("Users").child(userId).child("image").setValue(filePath)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Database write successful.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Database write failed.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                } else {
                    database.child("Users").child(userId).child("image").setValue("null")
                }

                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("userName", userName)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}