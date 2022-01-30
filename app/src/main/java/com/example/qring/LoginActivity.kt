package com.example.qring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    //interface for control auth
    lateinit var controlAuthStateListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initControlAuthStateListener()
        signUpTextView.setOnClickListener {
            val intentreg = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intentreg)
        }

        loginPageLogButton.setOnClickListener {
            if (editTextLogMail.text.toString().isNotEmpty() && editTextLogPassword.text.toString()
                    .isNotEmpty()
            ) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    editTextLogMail.text.toString(),
                    editTextLogPassword.text.toString()
                )
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login failed" + p0.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please fill in the empty fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        textViewlLogForgotPass.setOnClickListener {
            var dialogSendPass = ForgotPassFragment()
            dialogSendPass.show(supportFragmentManager, "showdialogpass")
        }

    }

    private fun initControlAuthStateListener() {

        controlAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.currentUser
                if (user != null) {
                    if (user.isEmailVerified) {
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT)
                            .show()
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "You haven't confirmed your account yet, please check your e-mail.",
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()

                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(controlAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(controlAuthStateListener)
    }
}