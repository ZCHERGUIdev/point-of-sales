package com.zcdev.pointofsale.fragments.Products.Adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.prod_viewcelll.view.*
import java.util.*


class ProductAdapter(val c: Context, val productList: MutableList<Product>, val tr:Boolean, val pr_tr_list:MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        //create inflater -> return viewholder()
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.prod_viewcelll, parent, false)

        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]

        //fetch data when update *use cached view ref !
        //holder.imageView.setImageResource(currentItem.productImg!!)
        holder.textView1.text = currentItem.productName
        holder.textView2.text = currentItem.productDesc
        Picasso.with(holder.itemView.context).load(currentItem.productImg).into(holder.img)



    }

    override fun getItemCount() = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //ref view attributes
        //val imageView: ImageView = itemView.imgProfil
        val textView1: TextView = itemView.prName
        val textView2: TextView = itemView.tvProd
        val img: ImageView = itemView.imgProd


      init {
          itemView.setOnClickListener(object : View.OnClickListener {
              override fun onClick(v: View?) {
                  val position: Int = adapterPosition
                  if (tr==true){
                      addProductToTransaction(productList, position)
                  }else{
                      // update
                      sendProductData(v!!, position)
                  }
              }
          })
          itemView.setOnLongClickListener(object : View.OnLongClickListener {
              override fun onLongClick(p0: View?): Boolean {
                  val position: Int = adapterPosition
                    // remove
                  removeAlert(p0!!, position)
                  return true
              }

          })
      }
        private fun removeAlert(p0: View, position: Int, ) {
            if (position != RecyclerView.NO_POSITION) {
                Toast.makeText(c, "item " + productList.get(position).productName, Toast.LENGTH_SHORT).show()

                if (itemView.getParent() != null) (itemView.getParent() as ViewGroup).removeView(itemView) // <- fix

                AlertDialog.Builder(c)
                        .setView(p0)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to delete this Product")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // remove product
                            removeProd(productList, position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
            }
        }

        private fun sendProductData(v:View, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val product = productList.get(position)
                //send product data to edit fragment
                val bundle = bundleOf(
                        "name" to product.productName,
                        "code" to product.productCode,
                        "desc" to product.productDesc,
                        "qte" to product.productQnt,
                        "img" to product.productImg)
                v!!.findNavController().navigate(R.id.action_productsFragment_to_editFragment, bundle)
            }
        }

        private fun removeProd(productList: MutableList<Product>, position: Int){
            val prdCode:String  = productList.get(position).productCode!!
            val ref = FirebaseDatabase.getInstance().reference
            val applesQuery: Query = ref.child("Products").orderByChild("productCode").equalTo(prdCode)

            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.removeValue()
                        productList.removeAt(position)

                        notifyDataSetChanged()
                        Toast.makeText(itemView.context, "item deleted", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException())
                }
            })
        }

        private fun addProductToTransaction(productList: MutableList<Product>, position: Int){
            if (position != RecyclerView.NO_POSITION) {
                Toast.makeText(c, "product added ", Toast.LENGTH_SHORT).show()
                pr_tr_list.add(productList.get(position))
                //dialaog specify quantity and price
                productList.removeAt(position)
                notifyDataSetChanged()
            }
        }


    }


}