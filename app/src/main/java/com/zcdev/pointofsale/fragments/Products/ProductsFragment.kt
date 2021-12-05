package com.zcdev.pointofsale.fragments.Products

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_products.view.*
import kotlinx.android.synthetic.main.prod_viewcell.*
import kotlinx.android.synthetic.main.product_row.view.*
import java.sql.DriverManager.println
import java.util.*


class ProductsFragment : Fragment() {
    var integrator:IntentIntegrator?=null

    companion object{
        var INSTANCE=ProductsFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_products, container, false)
        val lvProduct: ListView = view.lvProduct

        showProducts(view)

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
                var list_pro: MutableList<Product> = ArrayList<Product>()

                for (prosnap in dataSnapshot.children) {
                    val prod = prosnap.getValue(Product::class.java)
                    list_pro.add(prod!! as Product)
                }
                checkData(list_pro,v)

                if (container != null) {
                    lvProduct.adapter = MyCustomAdapter(container.getContext(), list_pro)
                }
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


    private class MyCustomAdapter(context: Context, products: MutableList<Product>): BaseAdapter(){
        private val mContext: Context
        private val mProducts: MutableList<Product>

        init {
            mContext = context
            mProducts = products
        }
        override fun getCount(): Int {
            return mProducts.size
        }

        override fun getItem(p0: Int): Any {
            return mProducts.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        // rendering each row
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.prod_viewcell, p2, false)

            val productName =  rowMain.prName
            productName.text = mProducts.get(p0).productName

            val productDesc = rowMain.tvProd
            productDesc.text = mProducts.get(p0).productDesc

            return rowMain;
        }
    }
}