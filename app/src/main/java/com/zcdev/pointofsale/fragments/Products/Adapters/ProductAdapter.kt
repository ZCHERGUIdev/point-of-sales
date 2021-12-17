package com.zcdev.pointofsale.fragments.Products.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.prod_viewcell.view.*
import kotlinx.coroutines.processNextEventInCurrentThread
import java.util.*


class ProductAdapter(val productList: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){



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
        Picasso.with(holder.itemView.context).load(currentItem.productImg).into(holder.img)



    }

    override fun getItemCount() = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //ref view attributes
        //val imageView: ImageView = itemView.imgProfil
        val textView1: TextView = itemView.prName
        val textView2: TextView = itemView.tvProd
        val img: ImageView = itemView.imgProd


      init {
          itemView.setOnClickListener(object:View.OnClickListener{
              override fun onClick(p0: View?) {
              p0!!.findNavController().navigate(R.id.action_productsFragment_to_editFragment)
              }

          })
          itemView.setOnLongClickListener(object :View.OnLongClickListener{
              override fun onLongClick(p0: View?): Boolean {
                  Toast.makeText(p0!!.context, "longggg", Toast.LENGTH_SHORT).show()
                  removeProd()

                  return true
              }

          })
      }

       private fun removeProd(){
            val ref = FirebaseDatabase.getInstance().reference
            val applesQuery: Query = ref.child("Products").orderByChild("productName").equalTo("prod1")

            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.removeValue()
                        Toast.makeText(itemView.context, "item deleted", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException())
                }
            })

        }


    }


}