package com.example.qring

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        regRegisterbutton.setOnClickListener{
            if(editTextRegEmail.text.toString().isNotEmpty() && editTextRegPass.text.toString().isNotEmpty() && editTextRegPassAgain.text.isNotEmpty()){
                if (editTextRegPass.text.toString().equals(editTextRegPassAgain.text.toString())){
                    registernew(editTextRegEmail.text.toString(),editTextRegPass.text.toString())
                }
                else{
                    Toast.makeText(this,"Passwords are not the same",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Please fill in the empty fields",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registernew(email: String, pass: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful) {
                        confirmmail()
                        var adddbuser = User()
                        adddbuser.username = editTextRegEmail.text.toString().substring(0,editTextRegEmail.text.toString().indexOf("@"))
                        adddbuser.user_id = FirebaseAuth.getInstance().currentUser?.uid
                        FirebaseDatabase.getInstance().reference
                            .child("user")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .setValue(adddbuser).addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    FirebaseAuth.getInstance().signOut()
                                    Toast.makeText(this@RegisterActivity,"Registration Successful",Toast.LENGTH_SHORT).show()
                                    gotologin()
                                }

                            }

                    }
                    else{
                        Toast.makeText(this@RegisterActivity,"Registration Failed"+p0.exception?.message,Toast.LENGTH_SHORT).show()

                    }
                }
            })
    }


    private fun confirmmail() {
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            user.sendEmailVerification()
                .addOnCompleteListener(object  : OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        if (p0.isSuccessful){
                            Toast.makeText(this@RegisterActivity,"Confirmation mail has been sent",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this@RegisterActivity,"Confirmation mail could not be sent"+p0.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }
    private fun gotologin(){
        var intent = Intent(this@RegisterActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}



