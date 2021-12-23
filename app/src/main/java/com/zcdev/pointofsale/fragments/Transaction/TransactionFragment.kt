package com.zcdev.pointofsale.fragments.Transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Client
import com.zcdev.pointofsale.data.models.Fournisseur
import com.zcdev.pointofsale.data.models.Product
import com.zcdev.pointofsale.fragments.Fournisseur.Adapters.TransactionAdapter
import com.zcdev.pointofsale.fragments.Products.Adapters.ProductAdapter
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_products.rvProduct
import kotlinx.android.synthetic.main.fragment_products.view.*
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.android.synthetic.main.fragment_transaction.view.*
import java.util.ArrayList


class TransactionFragment : Fragment() {

    var v:View?=null
    var trType:String?=""
    var arrayAdapter:ArrayAdapter<String>? =null
    var display_list: MutableList<Product> = ArrayList<Product>()

    override fun onResume() {
        super.onResume()
        if (arguments?.getString("tr").equals("entree")){
            trType = "Fournisseur"
            arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
                getFourniseurs())

        }else{
            trType = "Client"
            arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
                getClients())
        }
        v!!.autoCompleteTextView.setText(trType,false)
        v!!.autoCompleteTextView.setAdapter(arrayAdapter)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProducts()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_transaction, container, false)

        // Set Menu
        setHasOptionsMenu(true)
        val rvProducts: RecyclerView = v!!.rvProducts

        rvProducts.layoutManager = LinearLayoutManager(context)
        rvProducts.setHasFixedSize(true)
        v!!.addProduct.setOnClickListener{
          //  findNavController().navigate(R.id.action_productsFragment_to_addFragment)
        }


        return v
    }

    companion object {
        var INSTANCE= TransactionFragment()
    }

    private fun getFourniseurs():MutableList<String>{
        var list_fr: MutableList<String> = ArrayList<String>()

        //get fournisseurs from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Fournisseurs")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "Fournisseurs" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (frsnap in dataSnapshot.children) {
                    val fr = frsnap.getValue(Fournisseur::class.java)
                    list_fr.add(fr?.Name!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
        return list_fr
    }
    private fun getClients():MutableList<String>{
        var list_cl: MutableList<String> = ArrayList<String>()

        //get Clients from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Clients")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "Clients" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (clsnap in dataSnapshot.children) {
                    val cl = clsnap.getValue(Client::class.java)
                    list_cl.add(cl?.Name!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
        return list_cl
    }
    private fun showProducts(){
        var list_pro: MutableList<Product> = ArrayList<Product>()

        //get products from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "products" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (prosnap in dataSnapshot.children) {
                    val prod = prosnap.getValue(Product::class.java)
                    list_pro.add(prod!!)
                }
                display_list.addAll(list_pro)
                rvProducts.adapter = TransactionAdapter(activity!!, display_list)
                rvProducts.adapter!!.notifyDataSetChanged()

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }
}