package com.zcdev.pointofsale.fragments.Fournisseur.Adapters

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
import com.zcdev.pointofsale.data.models.Fournisseur
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fr_viewcell.view.*
import java.util.*


class FournisseurAdapter(val c: Context, val frList: MutableList<Fournisseur>) :
    RecyclerView.Adapter<FournisseurAdapter.FournisseurViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FournisseurViewHolder {
        //create inflater -> return viewholder()
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.fr_viewcell, parent, false)

        return FournisseurViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FournisseurViewHolder, position: Int) {
        val currentItem = frList[position]

        //fetch data when update *use cached view ref !
        //holder.imageView.setImageResource(currentItem.productImg!!)
        holder.textView1.text = currentItem.frName
        holder.textView2.text = currentItem.frPhone



    }

    override fun getItemCount() = frList.size

    inner class FournisseurViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //ref view attributes
        val textView1: TextView = itemView.frName
        val textView2: TextView = itemView.frPhone


      init {
          itemView.setOnClickListener(object : View.OnClickListener {
              override fun onClick(v: View?) {
                  val position: Int = adapterPosition
                    // update
                  sendFrData(v!!, position)
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
                Toast.makeText(c, "item " + frList.get(position).frName, Toast.LENGTH_SHORT).show()

                if (itemView.getParent() != null) (itemView.getParent() as ViewGroup).removeView(itemView) // <- fix

                AlertDialog.Builder(c)
                        .setView(p0)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to remove this Supplier")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // remove product
                            removeFr(frList, position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
            }
        }

        private fun sendFrData(v:View, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val fournisseur = frList.get(position)
                //send product data to edit fragment
                val bundle = bundleOf(
                        "id" to fournisseur.frId,
                        "name" to fournisseur.frName,
                        "phone" to fournisseur.frPhone,
                        "adr" to fournisseur.frAddress,
                        "eml" to fournisseur.frEmail)
                v!!.findNavController().navigate(R.id.action_fournisseurFragment_to_editFragmentFr, bundle)
            }
        }

       private fun removeFr(frList: MutableList<Fournisseur>, position: Int){
            val frName:String  = frList.get(position).frName!!
            val ref = FirebaseDatabase.getInstance().reference
            val applesQuery: Query = ref.child("Fournisseurs").orderByChild("frName").equalTo(frName)

            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.removeValue()
                        frList.removeAt(position)

                        notifyDataSetChanged()
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