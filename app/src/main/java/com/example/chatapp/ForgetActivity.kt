package com.example.chatapp

import com.example.chatapp.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity : AppCompatActivity() {
    private var editTextForget: TextInputEditText? = null
    private var buttonForget: Button? = null

    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        editTextForget = findViewById<TextInputEditText?>(R.id.editTextForget)
        buttonForget = findViewById<Button?>(R.id.buttonForget)

        buttonForget!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email = editTextForget!!.getText().toString()
                if (email != "") {
                    passwordReset(email)
                }
            }
        })

        auth = FirebaseAuth.getInstance()
    }

    fun passwordReset(email: String) {
        auth!!.sendPasswordResetEmail(email)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                            this@ForgetActivity,
                            "Please check your email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ForgetActivity,
                            "There is a problem.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }
}