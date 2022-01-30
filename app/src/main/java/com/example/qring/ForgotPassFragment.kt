package com.example.qring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth


class ForgotPassFragment : DialogFragment() {
    lateinit var emailEditText: EditText
    lateinit var mContext: FragmentActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_forgot_pass, container, false)
        emailEditText = view.findViewById(R.id.editTextSendPassword)
        mContext = requireActivity()
        var btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        var btnSend = view.findViewById<Button>(R.id.btnSend)
        btnSend.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailEditText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            mContext,
                            "Password recovery mail has been sent",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog?.dismiss()
                    } else {
                        Toast.makeText(
                            mContext,
                            "Error:" + task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }
        return view
    }


}