package com.example.qring

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_addproduct.*
import java.io.*
import java.util.*
import kotlin.random.Random
import android.graphics.Bitmap as Bitmap


class AddproductActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var dbaddproduct: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var bitmap: Bitmap
    private var productid = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val start = 111111
        val end = 9999999
        when (v?.id) {
            R.id.generateBTN -> {
                productid = rand(start, end).toString()
                var queryid = reference.child("Product")
                    .orderByChild("product_id")
                    .equalTo(productid)
                queryid.addListenerForSingleValueEvent((object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (singleSnapShot in snapshot!!.children) {
                            if (snapshot.child(productid).exists()) {
                                productid = rand(start, end).toString()
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }))

                val barCodeEncoder = BarcodeEncoder()
                bitmap = barCodeEncoder.encodeBitmap("$productid", BarcodeFormat.QR_CODE, 500, 500)
                qrCodeImageView.setImageBitmap(bitmap)
            }


        }
    }

    lateinit var intentIntegrator: IntentIntegrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addproduct)
        addProductToolbar.title = ""
        setSupportActionBar(addProductToolbar)
        intentIntegrator = IntentIntegrator(this)
        generateBTN.setOnClickListener(this)
        savebtn.setOnClickListener {
            addnewitem()
        }
        addProductToolbar.title = ""
        setSupportActionBar(addProductToolbar)
        addProductBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        dbaddproduct = FirebaseDatabase.getInstance();
        reference = dbaddproduct.getReference("Product")
    }

    fun rand(start: Int, end: Int): Int {
        require(!(start > end || end - start + 1 > Int.MAX_VALUE))
        return Random(System.nanoTime()).nextInt(end - start + 1) + start
    }

    private fun addnewitem() {
        if (productNameET.text.isNotEmpty() && expET.text.isNotEmpty() && amountET.text.isNotEmpty() && productid.isNotEmpty()) {
            if (checkid(productid)) {
                dbaddproduct = FirebaseDatabase.getInstance();
                reference = dbaddproduct.getReference("Product")
                var productname = productNameET.text.toString().trim()
                var producexp = expET.text.toString().trim()
                var productamount = amountET.text.toString().trim()
                var productqr = "".trim()
                var product_qrid = productid
                var productuserid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                var model = Product(
                    productname,
                    product_qrid,
                    producexp,
                    productamount,
                    productqr,
                    productuserid
                )
                var id = reference.push().key
                reference.child(id!!).setValue(model)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@AddproductActivity,
                                "Complated",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(this@AddproductActivity, "Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

            } else {
                Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                this@AddproductActivity,
                "Please try again without any spaces.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkid(productid: String): Boolean {
        var queryid = reference.child("Product")
            .orderByChild("product_id")
            .equalTo(productid)
        queryid.addListenerForSingleValueEvent((object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapShot in snapshot.children) {
                    if (snapshot.toString() != productid) {

                    } else {
                        Toast.makeText(
                            this@AddproductActivity,
                            "Product id already exist!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }))
        return true
    }


}


