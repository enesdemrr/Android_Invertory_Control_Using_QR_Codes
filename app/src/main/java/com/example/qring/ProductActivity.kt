package com.example.qring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.product_item.*

class ProductActivity : AppCompatActivity() {
    private lateinit var reference: DatabaseReference
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productArrayList: ArrayList<Product>
    private lateinit var refproduct: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        productToolbar.title = ""
        setSupportActionBar(productToolbar)
        buttonBackProduct.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        productRecyclerView = findViewById(R.id.productList)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.setHasFixedSize(true)
        productArrayList = arrayListOf<Product>()
        val dbref = FirebaseDatabase.getInstance()
        refproduct = dbref.getReference("Product")
        getProductData()

    }

    private fun getProductData() {
        reference = FirebaseDatabase.getInstance().reference
        var queryProduct = reference.child("Product").orderByChild("product_userid")
            .equalTo(FirebaseAuth.getInstance().currentUser?.uid.toString())
        queryProduct.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                productArrayList.clear()
                for (singleSnapShot in snapshot!!.children) {
                    var readproduct = singleSnapShot.getValue(Product::class.java)
                    if (readproduct != null) {
                        readproduct.product_qrcode = singleSnapShot.key
                        productArrayList.add(readproduct!!)
                    }

                }
                productRecyclerView.adapter = ProductAdapter(productArrayList, refproduct)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}




