package com.example.qring

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import java.util.ArrayList
import android.widget.*


class ProductAdapter(
    private val productList: ArrayList<Product>,
    private val prductref: DatabaseReference
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentitem = productList[position]
        holder.productName.text = currentitem.product_name
        holder.productExp.text = currentitem.product_explanation
        holder.productNum.text = currentitem.product_amount
        holder.imgdelbutton.setOnClickListener {
            val delsnack = Snackbar.make(
                holder.itemView,
                "Are you sure you want to delete?",
                Snackbar.LENGTH_INDEFINITE
            )
            delsnack.setAction("Yes") {
                prductref.child(currentitem.product_qrcode!!).removeValue()
            }.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productName: TextView = itemView.findViewById(R.id.productNameTv)
        val productExp: TextView = itemView.findViewById(R.id.productExpTv)
        val productNum: TextView = itemView.findViewById(R.id.productNumTv)
        val imgdelbutton = itemView.findViewById<ImageButton>(R.id.imgDelBtn)
    }
}
