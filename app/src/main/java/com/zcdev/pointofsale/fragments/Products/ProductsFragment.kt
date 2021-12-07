package com.zcdev.pointofsale.fragments.Products


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.zcdev.pointofsale.R

import com.zcdev.pointofsale.data.models.Product
import com.zcdev.pointofsale.fragments.Products.Adapters.ProductAdapter
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_products.view.*

import java.util.*


class ProductsFragment : Fragment() {
    var integrator:IntentIntegrator?=null
    var list_pro: MutableList<Product> = ArrayList<Product>()

    companion object{
        var INSTANCE=ProductsFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_products, container, false)

        // Set Menu
        setHasOptionsMenu(true)
        val rvProduct: RecyclerView = view.rvProduct

        showProducts(view)
        rvProduct.layoutManager = LinearLayoutManager(context)
        rvProduct.setHasFixedSize(true)

        view.addProd.setOnClickListener{
            findNavController().navigate(R.id.action_productsFragment_to_addFragment)
        }

        return view
    }


    private fun showProducts(v:View){
        var products = ArrayList<Product>()

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
                    list_pro.add(prod!! as Product)
                }
                rvProduct.adapter = ProductAdapter(list_pro)
                checkData(list_pro,v)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)


    }

    private fun checkData(list :MutableList<Product>,v:View) {
        if(list.isEmpty()){
            v.ivEmptyDoc.visibility=View.VISIBLE
            v.tvEmptyDoc.visibility=View.VISIBLE

        }else{
            v.ivEmptyDoc.visibility=View.GONE
            v.tvEmptyDoc.visibility=View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
        }
        return super.onOptionsItemSelected(item)
    }
}