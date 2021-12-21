package com.zcdev.pointofsale.fragments.Clients

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Client
import com.zcdev.pointofsale.data.models.Fournisseur
import kotlinx.android.synthetic.main.fragment_edit.view.edtNameUpdate
import kotlinx.android.synthetic.main.fragment_edit_cl.view.*
import kotlinx.android.synthetic.main.fragment_edit_fr.view.*
import kotlinx.android.synthetic.main.fragment_edit_fr.view.edtAddressUpdate
import kotlinx.android.synthetic.main.fragment_edit_fr.view.edtEmlUpdate
import kotlinx.android.synthetic.main.fragment_edit_fr.view.edtPhoneUpdate


class EditClFragment : Fragment() {

    var progdialog: ProgressDialog? = null
    var v:View?=null
    var id:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_edit_cl, container, false)


        //receive data
        id=arguments?.getString("id")
        v!!.edtNameUpdate.setText(arguments?.getString("name"))
        v!!.edtPhoneUpdate.setText(arguments?.getString("phone"))
        v!!.edtEmlUpdate.setText(arguments?.getString("eml"))
        v!!.edtAddressUpdate.setText(arguments?.getString("adr"))
        v!!.edtRdcUpdate.setText(arguments?.getString("rdc"))


        return v
    }

    //enable options menu in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
            editClient(v!!)
        }
        return super.onOptionsItemSelected(item)
    }



    fun editClient(v:View){
        // get values
        val name: String =  v.edtNameUpdate.text.toString()
        val phone: String = v.edtPhoneUpdate.text.toString()
        val address: String = v.edtAddressUpdate.text.toString()
        val email: String = v.edtEmlUpdate.text.toString()
        val reduction: String = v.edtRdcUpdate.text.toString()



        // create new client
        var cl = Client(id!!,name,phone,email,address,reduction)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Clients")

        // add fr to firebase
        myRef.child(id!!).setValue(cl).addOnSuccessListener{

            // navigate to fr list
            v!!.findNavController().navigate(R.id.action_editFragmentCl_to_clientFragment)

            }.addOnFailureListener{

            Toast.makeText(v.context, "Sorry !! item not Updated", Toast.LENGTH_SHORT).show()
        }
    }
}