package com.zcdev.pointofsale.fragments.Fournisseur

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Fournisseur
import com.zcdev.pointofsale.data.models.Versement
import kotlinx.android.synthetic.main.fragment_edit.view.edtNameUpdate
import kotlinx.android.synthetic.main.fragment_edit_fr.view.*


class EditFrFragment : Fragment() {

    var progdialog: ProgressDialog? = null
    var v:View?=null
    var id:String?=null
    lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_edit_fr, container, false)


        auth = FirebaseAuth.getInstance()

        //receive data
        id = arguments?.getString("id")
        v!!.edtNameUpdate.setText(arguments?.getString("name"))
        v!!.edtPhoneUpdate.setText(arguments?.getString("phone"))
        v!!.edtEmlUpdate.setText(arguments?.getString("eml"))
        v!!.edtAddressUpdate.setText(arguments?.getString("adr"))



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
            editFournisseur(v!!)
        }
        return super.onOptionsItemSelected(item)
    }



    fun editFournisseur(v:View){
        // get values
        val name: String =  v.edtNameUpdate.text.toString()
        val phone: String = v.edtPhoneUpdate.text.toString()
        val address: String = v.edtAddressUpdate.text.toString()
        val email: String = v.edtEmlUpdate.text.toString()



        // get fireabse database instance
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs").child(id!!)

        // update fr to firebase

        // update cl to firebase
        myRef.child("name").setValue(name)
        myRef.child("phone").setValue(phone)
        myRef.child("address").setValue(address)
        myRef.child("email").setValue(email)

            // navigate to fr list
            v!!.findNavController().navigate(R.id.action_editFragmentFr_to_fournisseurFragment)


    }
}