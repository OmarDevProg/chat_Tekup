package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MyLoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonSignin: Button
    private lateinit var buttonSignup: Button
    private lateinit var textViewForget: TextView

    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_login)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignin = findViewById(R.id.buttonSignin)
        buttonSignup = findViewById(R.id.buttonSignup)
        textViewForget = findViewById(R.id.textViewForget)

        auth = FirebaseAuth.getInstance()

        buttonSignin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signin(email, password)
            } else {
                Toast.makeText(this, "Please enter an email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        textViewForget.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
        }
    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Sign in is successful.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sign in is not successful.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
