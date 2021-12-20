package com.zcdev.pointofsale.fragments.Products.EditProduct

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_edit.view.*


class EditFragment : Fragment() {

    var v:View?=null
    var imageLink:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_edit, container, false)

        //receive data
        v!!.edtNameUpdate.setText(arguments?.getString("name"))
        v!!.edtBarcodeUpdate.setText(arguments?.getString("code"))
        v!!.edtDescUpdate.setText(arguments?.getString("desc"))
        v!!.edtQntUpdate.setText(arguments?.getString("qte"))

       // v!!.ivPickImageUpdate. (arguments?.getString("img"))

        return v
    }

    //enable options menu in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
            editProduct(v!!)
        }
        return super.onOptionsItemSelected(item)
    }



    fun editProduct(v:View){
        // get values

        val name: String =  v.edtNameUpdate.text.toString()
        val barcode: String = v.edtBarcodeUpdate.text.toString()
        val desc: String = v.edtDescUpdate.text.toString()
        val qnt: String = v.edtQntUpdate.text.toString()   // string to int --> Qte


        // add image !!
        var prd = Product(name,barcode,desc,qnt)


        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        // update product in firebase
        myRef.child(barcode).setValue(prd).addOnSuccessListener{

            // navigate to product list
            v!!.findNavController().navigate(R.id.action_editFragment_to_productsFragment)

            }.addOnFailureListener{

            Toast.makeText(v.context, "Sorry !! item not Updated", Toast.LENGTH_SHORT).show()
        }
    }


}