package com.zcdev.pointofsale.fragments.Versements

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.*
import com.zcdev.pointofsale.fragments.Clients.Adapters.ClientAdapter
import com.zcdev.pointofsale.fragments.Clients.ClientFragment
import com.zcdev.pointofsale.fragments.Versements.Adapters.VersementAdapter
import kotlinx.android.synthetic.main.fragment_client.*
import kotlinx.android.synthetic.main.fragment_versement.*
import kotlinx.android.synthetic.main.fragment_versement.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class VersementFragment : Fragment() {

    var v:View?=null
    var display_list: ArrayList<Versement> = ArrayList<Versement>()
    var hash_vrs : HashMap<String, Versement> = HashMap<String, Versement>()
    var cl:Client?=null
    var fr:Fournisseur?=null

    //progressDialog
    var progdialog: ProgressDialog? = null

    companion object{
        var INSTANCE= VersementFragment()
    }


    override fun onResume() {
        super.onResume()
        //receive data
        val id = arguments?.getString("id")!!
        val name = arguments?.getString("name")!!
        val phone = arguments?.getString("phone")!!
        val email = arguments?.getString("eml")!!
        val address = arguments?.getString("adr")!!

        val role = arguments?.getString("role")!!
        var reduction:String?=" "
        if(role.equals("CL")){
            reduction =arguments?.getString("rdc")
        }

        if (role.equals("CL")){

            cl = Client(id,name,phone,email,address,hash_vrs,role,reduction!!)

            rvVersements.adapter = VersementAdapter(requireActivity(), display_list, cl!!)
            rvVersements.adapter!!.notifyDataSetChanged()
            v!!.currentMoney.setText(cl!!.sommeVrs.toString())

        }else if (role.equals("FR")){
            fr = Fournisseur(id,name,phone,email,address,hash_vrs,role)

            rvVersements.adapter = VersementAdapter(requireActivity(), display_list, fr!!)
            rvVersements.adapter!!.notifyDataSetChanged()
            v!!.currentMoney.setText(fr!!.sommeVrs.toString())

        }
        showVersements()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_versement, container, false)

        // Set Menu
        setHasOptionsMenu(true)
        val rvVersements: RecyclerView = v!!.rvVersements

        rvVersements.layoutManager = LinearLayoutManager(context)
        rvVersements.setHasFixedSize(true)


        v!!.addVrs.setOnClickListener{
            //---------------add Versement to Client Or Fournisseur-------
            versementDialogue()
        }

        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_product_menu, menu)
    }


//-----------------------------------------edit trader (client / fournisseur)-------------------------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if(item.itemId == R.id.menu_edit){

        if (cl!=null) {
            val bundle = bundleOf(
                    "id" to cl!!.Id,
                    "name" to cl!!.Name,
                    "phone" to cl!!.Phone,
                    "adr" to cl!!.Address,
                    "eml" to cl!!.Email,
                    "role" to cl!!.role,
                    "rdc" to cl!!.reduction)
            findNavController().navigate(R.id.action_versementFragment_to_editFragmentCl,bundle)

        }else if (fr!=null) {
            val bundle = bundleOf(
                    "id" to fr!!.Id,
                    "name" to fr!!.Name,
                    "phone" to fr!!.Phone,
                    "adr" to fr!!.Address,
                    "eml" to fr!!.Email,
                    "role" to fr!!.role)
            findNavController().navigate(R.id.action_versementFragment_to_editFragmentFr,bundle)
        }

    }
        return super.onOptionsItemSelected(item)
    }

    private fun versementDialogue() {
        // Set up the input
        val input = EditText(context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("verser ici ...")
        input.inputType = InputType.TYPE_CLASS_NUMBER


            AlertDialog.Builder(context)
                    .setView(input)
                    .setTitle("add Versement")
                    .setIcon(R.drawable.ic_money)
                    .setPositiveButton("Add") { dialog, _ ->
                        // add versement
                        addVersement(input.text.toString().toInt())
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }

    private fun addVersement(montant:Int){

        // get values
        val date = Calendar.getInstance().time // it's unique (dateTime)
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val id:String= date.toString()
        // create new versement
        var vrs = Versement(id,formatedDate.toString(),montant)
        display_list.add(vrs)
        hash_vrs.put(vrs.Id!!,vrs)

        if (cl!=null) {
            val myRef = database.getReference("Clients/"+cl!!.Id+"/versements")
            val clRef = database.getReference("Clients/"+cl!!.Id)

            // add versement to firebase
            myRef.child(id).setValue(vrs)
            // update client somme vrs
            cl!!.sommeVrs = cl!!.calculeSV(hash_vrs)
            clRef.child("sommeVrs").setValue(cl!!.sommeVrs)

            v!!.currentMoney.setText(cl!!.sommeVrs.toString())


        }else if (fr!=null) {
            val myRef = database.getReference("Fournisseurs/"+fr!!.Id+"/versements")
            val frRef = database.getReference("Fournisseurs/"+fr!!.Id)

            // add versement to firebase
            myRef.child(id).setValue(vrs)
            // update fournisseur somme vrs

            fr!!.sommeVrs = fr!!.calculeSV(hash_vrs)
            frRef.child("sommeVrs").setValue(fr!!.sommeVrs )

            v!!.currentMoney.setText(fr!!.sommeVrs.toString())
        }

        rvVersements.adapter!!.notifyDataSetChanged()


    }

    private fun showVersements(){
        var list_vrs = ArrayList<Versement>()

        //get verements from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        //Get datasnapshot at your "versements" root node

        progdialog!!.show()


        if (cl!=null) {
            val myRef = database.getReference("Clients/"+cl!!.Id+"/versements")
            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of vrs in datasnapshot

                    for (vrssnap in dataSnapshot.children) {
                        val vrs = vrssnap.getValue(Versement::class.java)
                        list_vrs.add(vrs!!)
                    }
                    display_list.addAll(list_vrs)
                    rvVersements.adapter = VersementAdapter(activity!!, display_list,cl!!)
                    rvVersements.adapter!!.notifyDataSetChanged()

                    progdialog!!.hide()
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            myRef.addListenerForSingleValueEvent(eventListener)

        }else if (fr!=null) {
            val myRef = database.getReference("Fournisseurs/"+fr!!.Id+"/versements")
            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of vrs in datasnapshot

                    for (vrssnap in dataSnapshot.children) {
                        val vrs = vrssnap.getValue(Versement::class.java)
                        list_vrs.add(vrs!!)
                    }
                    display_list.addAll(list_vrs)
                    rvVersements.adapter = VersementAdapter(activity!!, display_list,fr!!)
                    rvVersements.adapter!!.notifyDataSetChanged()

                    progdialog!!.hide()
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            myRef.addListenerForSingleValueEvent(eventListener)
        }
    }



}