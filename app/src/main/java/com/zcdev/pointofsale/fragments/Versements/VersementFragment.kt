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
import kotlinx.android.synthetic.main.doc_viewcell.*
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
       var view =  inflater.inflate(R.layout.fragment_versement, container, false)

        // Set Menu
        setHasOptionsMenu(true)
        setupRecycler(view)


        view!!.addVrs.setOnClickListener{
            //---------------add Versement to Client Or Fournisseur-------
            versementDialogue(view)
        }

        //receive data
        val id = arguments?.getString("id")!!
        val name = arguments?.getString("name")!!
        val phone = arguments?.getString("phone")!!
        val email = arguments?.getString("eml")!!
        val address = arguments?.getString("adr")!!
        hash_vrs = arguments?.getSerializable("vrsHash")!! as HashMap<String, Versement>
        val balance = arguments?.getString("balance")!!

        val role = arguments?.getString("role")!!
        var reduction:String?=" "
        if(role.equals("CL")){
            reduction =arguments?.getString("rdc")
        }

        if (role.equals("CL")){

            cl = Client(id,name,phone,email,address,hash_vrs,role,reduction!!)
            cl!!.sommeVrs = cl!!.calculeSV(hash_vrs)
            cl!!.balance = balance.toDouble()
            view.rvVersements.adapter = VersementAdapter(requireActivity(), display_list, cl!!,view!!)
            view.rvVersements.adapter!!.notifyDataSetChanged()
            view!!.currentMoney.setText(cl!!.sommeVrs.toString())

        }else if (role.equals("FR")){
            fr = Fournisseur(id,name,phone,email,address,hash_vrs,role)
            fr!!.sommeVrs = fr!!.calculeSV(hash_vrs)
            fr!!.balance = balance.toDouble()

            view.rvVersements.adapter = VersementAdapter(requireActivity(), display_list, fr!!,view!!)
            view.rvVersements.adapter!!.notifyDataSetChanged()
            view!!.currentMoney.setText(fr!!.sommeVrs.toString())

        }
        showVersements(view)


        return view
    }

    private fun setupRecycler(view: View) {
        var trader=Trader()
        view.rvVersements.layoutManager = LinearLayoutManager(requireContext())
        view.rvVersements.setHasFixedSize(true)

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

    private fun versementDialogue(view:View) {
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
                        addVersement(input.text.toString().toDouble(),view)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }

    private fun addVersement(montant:Double,view: View){

        // get values
        val date = Calendar.getInstance().time // it's unique (dateTime)
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val id:String= date.toString()
        var balance:Double?
        // create new versement
        var vrs = Versement(id,formatedDate.toString(),montant)
        display_list.add(vrs)
        hash_vrs.put(vrs.Id!!,vrs)

        if (cl!=null) {
            val myRef = database.getReference("Clients/"+cl!!.Id+"/versements")
            val clRef = database.getReference("Clients/"+cl!!.Id)

            // add versement to firebase
            myRef.child(id).setValue(vrs)

            // update client somme vrs and vrsHash
            cl!!.versements!!.putAll(hash_vrs)
            cl!!.sommeVrs = cl!!.calculeSV(hash_vrs)
            clRef.child("sommeVrs").setValue(cl!!.sommeVrs)
            clRef.child("versements").setValue(cl!!.versements)

            // calculate balance
            balance  = cl!!.balance!! + vrs.montant!!
            cl!!.balance = balance
            // update balance of trader -----------------------------------------------------
            clRef.child("balance").setValue(balance)

            view.currentMoney.setText(cl!!.sommeVrs.toString())


        }else if (fr!=null) {
            val myRef = database.getReference("Fournisseurs/"+fr!!.Id+"/versements")
            val frRef = database.getReference("Fournisseurs/"+fr!!.Id)

            // add versement to firebase
            myRef.child(id).setValue(vrs)
            // update fournisseur somme vrs

            // update client somme vrs and vrsHash
            fr!!.versements!!.putAll(hash_vrs)
            fr!!.sommeVrs = fr!!.calculeSV(hash_vrs)
            frRef.child("sommeVrs").setValue(fr!!.sommeVrs)
            frRef.child("versements").setValue(fr!!.versements)

            // calculate balance
            balance  = fr!!.balance!! + vrs.montant!!
            fr!!.balance = balance
            // update balance of trader -----------------------------------------------------
            frRef.child("balance").setValue(balance)

            view.currentMoney.setText(fr!!.sommeVrs.toString())
        }

        rvVersements.adapter!!.notifyDataSetChanged()


    }

    private fun showVersements(view: View){

        if (cl!=null) {getVersements(cl!!,view)
        }else if (fr!=null) { getVersements(fr!!,view) }
    }

    private fun getVersements(trader:Trader,view: View){
        progdialog!!.show()

        for(v in trader.versements!!){
            display_list.add(v.value)
        }
        view.rvVersements.adapter = VersementAdapter(requireContext(), display_list,trader,view)
        view.rvVersements.adapter!!.notifyDataSetChanged()

        progdialog!!.hide()
    }
}