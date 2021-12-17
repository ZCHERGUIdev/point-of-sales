package com.zcdev.pointofsale.fragments.Products


import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
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
    //progressDialog
     var progdialog: ProgressDialog? = null



    companion object{
        var INSTANCE=ProductsFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")


        showProducts()

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



        rvProduct.layoutManager = LinearLayoutManager(context)
        rvProduct.setHasFixedSize(true)
        rvProduct.adapter = ProductAdapter(list_pro)
        view.addProd.setOnClickListener{
            findNavController().navigate(R.id.action_productsFragment_to_addFragment)
        }

        return view
    }


    private fun showProducts(){
        var products = ArrayList<Product>()

        //get products from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "products" root node
        progdialog!!.show()
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (prosnap in dataSnapshot.children) {
                    val prod = prosnap.getValue(Product::class.java)
                    list_pro.add(prod!!)
                }
                rvProduct.adapter = ProductAdapter(list_pro)
                rvProduct.adapter!!.notifyDataSetChanged()

                checkData(list_pro)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)


    }
// remove prod
    fun removeProd(){
        val ref = FirebaseDatabase.getInstance().reference
        val applesQuery: Query = ref.child("Product").orderByChild("productName").equalTo(list_pro[0].productName)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException())
            }
        })

    }

    private fun checkData(list :MutableList<Product>) {

        if(list.isEmpty()){
            ivEmptyDoc.visibility=View.VISIBLE
            tvEmptyDoc.visibility=View.VISIBLE
            progdialog!!.hide()


        }else{
            ivEmptyDoc.visibility=View.GONE
            tvEmptyDoc.visibility=View.GONE
            progdialog!!.hide()

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