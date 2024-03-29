package com.zcdev.pointofsale.fragments.Products.Adapters

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.prod_viewcelll.view.*
import java.util.*


class ProductAdapter(val c: Context, val productList: MutableList<Product>, val tr: Boolean,val trType:String, val pr_tr_list: MutableList<Product>,var auth:FirebaseAuth) :
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
        holder.textView1.text = currentItem.productName
        holder.textView2.text = currentItem.productDesc
        holder.textView3.text = currentItem.productQnt.toString()
        Picasso.with(holder.itemView.context).load(currentItem.productImg).into(holder.img)



    }

    override fun getItemCount() = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //ref view attributes
        val textView1: TextView = itemView.prName
        val textView2: TextView = itemView.tvProd
        val textView3: TextView = itemView.qte
        val img: ImageView = itemView.imgProd


      init {
          itemView.setOnClickListener(object : View.OnClickListener {
              override fun onClick(v: View?) {
                  val position: Int = adapterPosition
                  if (tr == true) {
                      addProductToTransaction(productList, position)
                  } else {
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
        private fun removeAlert(p0: View, position: Int) {
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

        private fun sendProductData(v: View, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val product = productList.get(position)
                //send product data to edit fragment
                val bundle = bundleOf(
                        "name" to product.productName,
                        "code" to product.productCode,
                        "desc" to product.productDesc,
                        "qte" to product.productQnt.toString(),
                        "prixA" to product.prixAchat.toString(),
                        "prixV" to product.prixVente.toString(),
                        "img" to product.productImg)
                v!!.findNavController().navigate(R.id.action_productsFragment_to_editFragment, bundle)
            }
        }

        private fun removeProd(productList: MutableList<Product>, position: Int){
            val currentUser =auth.currentUser
            val prdCode:String  = productList.get(position).productCode!!
            val ref = FirebaseDatabase.getInstance().reference
            val applesQuery: Query = ref.child("Users/"+ currentUser!!.uid +"/Products").orderByChild("productCode").equalTo(prdCode)

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
                // add qte , price
                var prd:Product = productList.get(position)
                showAlert(prd,productList,position)
                Toast.makeText(c, "product added ", Toast.LENGTH_SHORT).show()
            }
        }

        private fun showAlert(prd: Product,prdList: MutableList<Product>,position: Int){

            val layout = LinearLayout(c)
            layout.orientation = LinearLayout.VERTICAL

            val inputQte = EditText(c)
            inputQte.setHint("quantite")
            inputQte.inputType = InputType.TYPE_CLASS_NUMBER
            layout.addView(inputQte)

            val inputprice = EditText(c)

            // prix achat / vente selon trType------------------------------------------------------
            if (trType.equals("Client")){
                inputprice.setText(prd.prixVente.toString())
            }else if (trType.equals("Fournisseur")){
                inputprice.setText(prd.prixAchat.toString())
            }


            inputprice.inputType = InputType.TYPE_CLASS_NUMBER
            layout.addView(inputprice)


            AlertDialog.Builder(c)
                    .setView(layout)
                    .setTitle("add Product")
                    .setIcon(R.drawable.ic_main_menu_goods)
                    .setPositiveButton("Add") { dialog, _ ->

                        var qte:Int = inputQte.text.toString().toInt()


                            // update price Achat/vente depends on trType
                            if (trType.equals("Client")){
                                prd.prixVente = inputprice.text.toString().toDouble()
                                if (prd.productQnt!! < qte) {
                                    // prd not available
                                    inputQte.setText(" ")
                                    Toast.makeText(c, "quantity not available ", Toast.LENGTH_SHORT).show()
                                    return@setPositiveButton
                                }
                            }else if (trType.equals("Fournisseur")){
                                prd.prixAchat = inputprice.text.toString().toDouble()
                            }
                            // add product to transaction
                            prd.productQnt = qte
                            pr_tr_list.add(prd)
                            prdList.removeAt(position)
                            notifyDataSetChanged()

                            dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }


    }


}