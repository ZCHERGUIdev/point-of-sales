package com.zcdev.pointofsale.fragments.Fournisseur.Adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import com.zcdev.pointofsale.fragments.Products.ProductsFragment
import kotlinx.android.synthetic.main.prod_viewcell.view.*
import kotlinx.android.synthetic.main.prod_viewcelll.view.prName


class TransactionAdapter(val c: Context, val productList: MutableList<Product>,val trType:String) :
    RecyclerView.Adapter<TransactionAdapter.ProductViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            //create inflater -> return viewholder()
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.prod_viewcell, parent, false)

            return ProductViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val currentItem = productList[position]

            //fetch data when update *use cached view ref !
            holder.textView1.text = currentItem.productName
            holder.textView2.text = currentItem.productCode
            holder.textView3.text = currentItem.productQnt.toString()

            // depends on transaction
            if (trType.equals("Fournisseur")){
                holder.textView4.text = currentItem.prixAchat.toString()
            }else if(trType.equals("Client")){
                holder.textView4.text = currentItem.prixVente.toString()
            }
        }

        override fun getItemCount() = productList.size

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            //ref view attributes
            val textView1: TextView = itemView.prName
            val textView2: TextView = itemView.bCodeProd
            val textView3: TextView = itemView.qteProd
            val textView4: TextView = itemView.prix


            init {
                itemView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        val position: Int = adapterPosition
                        // remove from transaction
                        removeAlert(p0!!, position)
                        return true
                    }

                })
            }
            private fun removeAlert(p0: View, position: Int, ) {
                if (position != RecyclerView.NO_POSITION) {
                    Toast.makeText(
                        c,
                        "item " + productList.get(position).productName,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (itemView.getParent() != null) (itemView.getParent() as ViewGroup).removeView(
                        itemView
                    ) // <- fix

                    AlertDialog.Builder(c)
                        .setView(p0)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to remove this Product from current transaction ?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // remove product
                            productList.removeAt(position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        }
}