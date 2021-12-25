package com.zcdev.pointofsale.fragments.Versements.Adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Trader
import com.zcdev.pointofsale.data.models.Versement
import kotlinx.android.synthetic.main.vrs_viewcell.view.*


class VersementAdapter(val c: Context, val versementList: MutableList<Versement>, val trader:Trader) :
    RecyclerView.Adapter<VersementAdapter.VersementViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersementViewHolder {
            //create inflater -> return viewholder()
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.vrs_viewcell, parent, false)

            return VersementViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: VersementViewHolder, position: Int) {
            val currentItem = versementList[position]

            //fetch data when update *use cached view ref !
            holder.textView1.text = currentItem.montant.toString()
            holder.textView2.text = currentItem.date

        }

        override fun getItemCount() = versementList.size

        inner class VersementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            //ref view attributes
            val textView1: TextView = itemView.vrs
            val textView2: TextView = itemView.vrs_date


            init {
                itemView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        val position: Int = adapterPosition
                        // remove versement
                        removeAlert(p0!!, position)
                        return true
                    }

                })
            }

            private fun removeAlert(p0: View, position: Int, ) {
                if (position != RecyclerView.NO_POSITION) {
                    Toast.makeText(
                        c,
                        "item " + versementList.get(position).date,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (itemView.getParent() != null) (itemView.getParent() as ViewGroup).removeView(
                        itemView
                    ) // <- fix

                    AlertDialog.Builder(c)
                        .setView(p0)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to remove this versement ?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // remove versement
                            removeVr(versementList, position)

                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }

            private fun removeVr(versementList: MutableList<Versement>, position: Int){
                val idVrs: String? = versementList.get(position).Id
                val ref = FirebaseDatabase.getInstance().reference
                var applesQuery:Query?=null

                // remove versement from client or fournisseur versements list
                if (trader.role.equals("CL")){
                    applesQuery = ref.child("Clients/"+trader.Id+"/versements").orderByChild("id").equalTo(idVrs)
                }else if(trader.role.equals("FR")){
                    applesQuery = ref.child("Fournisseurs/"+trader.Id+"/versements").orderByChild("id").equalTo(idVrs)
                }

                applesQuery!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (appleSnapshot in dataSnapshot.children) {
                            appleSnapshot.ref.removeValue()
                            versementList.removeAt(position)

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