package com.example.qring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.qring.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_addproduct.*
import kotlinx.android.synthetic.main.activity_addproduct.view.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var reference: DatabaseReference
    lateinit var controlAuthStateListener: FirebaseAuth.AuthStateListener
    private lateinit var binding: ActivityMainBinding
    lateinit var parent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainToolbar.setLogo(R.drawable.qr_logo)
        mainToolbar.title = "QRing"
        setSupportActionBar(mainToolbar)
        initControlAuthStateListener()
        intentIntegrator = IntentIntegrator(this)
        //   var userid = FirebaseAuth.getInstance().currentUser?.uid
        scanBtn.setOnClickListener(this)
        var usernametext = FirebaseAuth.getInstance().currentUser?.email
        userNameTextV.setText(
            usernametext.toString().substring(0, usernametext.toString().indexOf("@"))
        )
        mainFab.setOnClickListener {
            onAddButtonClicked()
        }
        addFab.setOnClickListener {
            val intent = Intent(this, AddproductActivity::class.java)
            startActivity(intent)
        }
        categoryFab.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }
        mainUpdateBtn.setOnClickListener(this)
        buttonResult.setOnClickListener(this)


    }

    private fun readproducts() {
        reference = FirebaseDatabase.getInstance().reference
        var querytry = reference.child("Product")
            .orderByChild("product_id")
            .equalTo(textViewProductId.text.toString())
        querytry.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pathString: DataSnapshot) {
                for (singleSnapShot in pathString!!.children) {
                    var readproduct = singleSnapShot.getValue(Product::class.java)
                    editTextProductName.setText(readproduct?.product_name)
                    editTextTProdcutExp.setText(readproduct?.product_explanation)
                    editTextNumber.setText(readproduct?.product_amount)
                    parent = singleSnapShot.key.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun updateProduct(
        productname: String,
        productexp: String,
        productnum: String
    ) {
        if (editTextProductName.text.isNotEmpty() && editTextTProdcutExp.text.isNotEmpty() && editTextNumber.text.isNotEmpty()) {


            val product = mapOf<String, String>(
                "product_name" to productname,
                "product_explanation" to productexp,
                "product_amount" to productnum
            )
            reference.child("Product").child(parent).updateChildren(product).addOnSuccessListener {
                binding.editTextProductName.text.clear()
                binding.editTextTProdcutExp.text.clear()
                binding.editTextNumber.text.clear()
                Toast.makeText(this, "Update Successfuly", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "Do not leave any blank space!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initControlAuthStateListener() {
        controlAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.currentUser
                if (user != null) {

                } else {
                    var intent = Intent(this@MainActivity, LoginActivity::class.java)
                    //After the user logs off, to prevent user from returning to the main activity using the back key.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.scanBtn -> {
                intentIntegrator.initiateScan()

            }
        }
        buttonResult.setOnClickListener {
            readproducts()
        }
        binding.mainUpdateBtn.setOnClickListener {
            // val productt_id = binding.textViewProductId.text.toString()
            val productname = binding.editTextProductName.text.toString()
            val productexp = binding.editTextTProdcutExp.text.toString()
            val productnum = binding.editTextNumber.text.toString()
            updateProduct(productname, productexp, productnum)
        }


    }

    lateinit var intentIntegrator: IntentIntegrator
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_animation
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents != null) {
                var na = intentResult.contents
                textViewProductId.text = String.format(na.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menulogout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(controlAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if (controlAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(controlAuthStateListener)
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)

        clicked = !clicked

    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            addFab.visibility = View.VISIBLE
            categoryFab.visibility = View.VISIBLE
        } else {
            addFab.visibility = View.INVISIBLE
            categoryFab.visibility = View.INVISIBLE

        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            addFab.startAnimation(fromBottom)
            categoryFab.startAnimation(fromBottom)
            mainFab.startAnimation(rotateOpen)
        } else {
            addFab.startAnimation(toBottom)
            categoryFab.startAnimation(toBottom)
            mainFab.startAnimation(rotateClose)

        }

    }


}
