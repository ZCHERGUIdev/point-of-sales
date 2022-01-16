package com.zcdev.pointofsale.fragments.Clients.Adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Client
import kotlinx.android.synthetic.main.cl_viewcell.view.*


class ClientAdapter(val c: Context, val clList: MutableList<Client>,var auth: FirebaseAuth) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        //create inflater -> return viewholder()
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.cl_viewcell, parent, false)

        return ClientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val currentItem = clList[position]

        //fetch data when update *use cached view ref !
        holder.textView1.text = currentItem.Name
        holder.textView2.text = currentItem.Phone
        holder.textView3.text = currentItem.balance.toString()



    }

    override fun getItemCount() = clList.size

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //ref view attributes
        val textView1: TextView = itemView.clName
        val textView2: TextView = itemView.clPhone
        val textView3: TextView = itemView.balance


      init {
          itemView.setOnClickListener(object : View.OnClickListener {
              override fun onClick(v: View?) {
                  val position: Int = adapterPosition
                    // view details
                  sendClData(v!!, position)
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
                Toast.makeText(c, "item " + clList.get(position).Name, Toast.LENGTH_SHORT).show()

                if (itemView.getParent() != null) (itemView.getParent() as ViewGroup).removeView(itemView) // <- fix

                AlertDialog.Builder(c)
                        .setView(p0)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to remove this Client")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // remove client
                            removeCl(clList, position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
            }
        }

        private fun sendClData(v:View, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val client = clList.get(position)
                if (client.versements==null) client.versements = HashMap()
                //view versement client
                val bundle = bundleOf(
                        "id" to client.Id,
                        "name" to client.Name,
                        "phone" to client.Phone,
                        "adr" to client.Address,
                        "eml" to client.Email,
                        "role" to client.role,
                        "rdc" to client.reduction,
                        "balance" to client.balance.toString(),
                        "vrsHash" to client.versements)
                v!!.findNavController().navigate(R.id.action_clientFragment_to_versementFragment, bundle)
            }
        }

       private fun removeCl(clList: MutableList<Client>, position: Int){
            val currentUser =auth.currentUser
            val clName:String  = clList.get(position).Name!!
            val ref = FirebaseDatabase.getInstance().reference
            val applesQuery: Query = ref.child("Users/"+ currentUser!!.uid +"/Clients").orderByChild("name").equalTo(clName)

            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.removeValue()
                        clList.removeAt(position)

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