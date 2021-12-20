package com.zcdev.pointofsale.fragments.Fournisseur

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.CaptureAct
import com.zcdev.pointofsale.data.models.Fournisseur
import kotlinx.android.synthetic.main.fragment_add.view.edtName
import kotlinx.android.synthetic.main.fragment_add_fr.view.*


class AddFrFragment : Fragment(){

    var progdialog: ProgressDialog? = null
    var v:View?=null
    var id:String?=null

    companion object{
        var INSTANCE=AddFrFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_fr, container, false)
        // Set Menu
        setHasOptionsMenu(true)

        return v
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
                addFournisseur(v!!)
            // navigate to product list
            findNavController().navigate(R.id.action_addFragmentFr_to_fournisseurFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addFournisseur(v: View){
        // Inflate the layout for this fragment

        // get values
        val name: String =  v.edtName.text.toString()
        val phone: String = v.edtPhone.text.toString()
        val address: String = v.edtAddress.text.toString()
        val email: String = v.edtEml.text.toString()



        id=name+phone // id need to be hached !!!
        // create new fournisseur
        var fr = Fournisseur(id!!,name,phone,email,address)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Fournisseurs")

        // add product to firebase
        myRef.child(id!!).setValue(fr)
    }
}







