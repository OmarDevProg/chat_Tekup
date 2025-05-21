package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class MyLoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonSignin: Button
    private lateinit var buttonSignup: Button
    private lateinit var textViewForget: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editTextEmail=findViewById(R.id.EditTextEmail)
        editTextPassword=findViewById(R.id.EditTextPassword)
        buttonSignin=findViewById(R.id.buttonSignin)
        buttonSignup=findViewById(R.id.buttonSignup)
        textViewForget=findViewById(R.id.textViewForget)

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
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        textViewForget.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
                }
            }

    }
}



