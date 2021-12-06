package com.zcdev.pointofsale.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.prod_viewcell.view.*

class ProductAdapter(private val productList: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        //create inflater -> return viewholder()
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.prod_viewcell, parent, false)

        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]

        //fetch data when update *use cached view ref !
        //holder.imageView.setImageResource(currentItem.productImg!!)
        holder.textView1.text = currentItem.productName
        holder.textView2.text = currentItem.productDesc
    }

    override fun getItemCount() = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //ref view attributes
        //val imageView: ImageView = itemView.imgProfil
        val textView1: TextView = itemView.prName
        val textView2: TextView = itemView.tvProd
    }
}