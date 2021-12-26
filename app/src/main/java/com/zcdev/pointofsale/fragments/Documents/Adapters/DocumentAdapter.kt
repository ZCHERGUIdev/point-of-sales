package com.zcdev.pointofsale.fragments.Documents.Adapters

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
import com.zcdev.pointofsale.data.models.Transaction
import kotlinx.android.synthetic.main.doc_viewcell.view.*
import kotlinx.android.synthetic.main.prod_viewcell.view.qteProd
import java.text.SimpleDateFormat


class DocumentAdapter(val c: Context, val docList: MutableList<Transaction>) :
    RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
            //create inflater -> return viewholder()
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.doc_viewcell, parent, false)

            return DocumentViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
            val currentItem = docList[position]

            //fetch data when update *use cached view ref !
            holder.textView1.text = currentItem.type
            holder.textView2.text = currentItem.trader
            holder.textView3.text = currentItem.date
            holder.textView4.text = currentItem.qteProd.toString()
            holder.textView5.text = currentItem.prixTotal.toString()

        }

        override fun getItemCount() = docList.size

        inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            //ref view attributes
            val textView1: TextView = itemView.doc
            val textView2: TextView = itemView.trader
            val textView3: TextView = itemView.doc_date
            val textView4: TextView = itemView.qteProd
            val textView5: TextView = itemView.prixtotal


            init {
                itemView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        val position: Int = adapterPosition
                        // remove transaction from documents
                        removeAlert(p0!!, position)
                        return true
                    }

                })
            }
            private fun removeAlert(p0: View, position: Int, ) {
                if (position != RecyclerView.NO_POSITION) {
                    Toast.makeText(
                        c,
                        "item " + docList.get(position).Id,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (itemView.getParent() != null) (itemView.getParent() as ViewGroup).removeView(
                        itemView
                    ) // <- fix

                    AlertDialog.Builder(c)
                        .setView(p0)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to remove this Transaction ?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // remove product
                            removeDoc(docList, position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }

            private fun removeDoc(docList: MutableList<Transaction>, position: Int){
                val docId:String  = docList.get(position).Id!!
                val ref = FirebaseDatabase.getInstance().reference
                val applesQuery: Query = ref.child("Transactions").orderByChild("id").equalTo(docId)

                applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (appleSnapshot in dataSnapshot.children) {
                            appleSnapshot.ref.removeValue()
                            docList.removeAt(position)

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