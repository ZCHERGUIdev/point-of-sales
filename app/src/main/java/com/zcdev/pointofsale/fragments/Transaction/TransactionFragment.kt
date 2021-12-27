package com.zcdev.pointofsale.fragments.Transaction

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.*
import com.zcdev.pointofsale.fragments.Fournisseur.Adapters.TransactionAdapter
import kotlinx.android.synthetic.main.doc_viewcell.*
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.android.synthetic.main.fragment_transaction.view.*
import kotlinx.android.synthetic.main.fragment_versement.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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

        if (arguments?.getSerializable("prds")!=null){
            display_list = arguments?.getSerializable("prds") as ArrayList<Product>
        }
        rvProducts.adapter = TransactionAdapter(requireActivity(), display_list, trType!!)
        rvProducts.adapter!!.notifyDataSetChanged()


        v!!.autoCompleteTextView.setText(trType, false)
        v!!.autoCompleteTextView.setAdapter(arrayAdapter)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

//-----------------------------------------add product to transaction-------------------------------------------
            //send bundel (tr!!) if tr then add product to transaction else list products
            val bundle = bundleOf(
                    "src" to "transaction",
                    "tr" to trType)
            findNavController().navigate(R.id.action_transactionFragment_to_productsFragment, bundle)
        }

        return v
    }

    companion object {
        var INSTANCE= TransactionFragment()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
    }



//-----------------------------------------add transaction-------------------------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do

                versementDialogue( v!!.autoCompleteTextView.text.toString())
                // proposer d'ajouter un versement !!
                    //get trader from trType(client / fournisseur) and name (view)
                        // add versement to this trader
            addTransaction(display_list, v!!.autoCompleteTextView.text.toString())

            findNavController().navigate(R.id.action_transactionFragment_to_dashboardFragment)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun versementDialogue(trader: String) {
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
                    addVersement(input.text.toString().toInt(), trader)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }
    private fun addVersement(montant: Int, trader: String){


        // get values
        val date = Calendar.getInstance().time // it's unique (dateTime)
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val id:String= date.toString()
        // create new versement
        var vrs = Versement(id, formatedDate.toString(), montant)

        if(trType.equals("Fournisseur")){
            val traderRef = database.getReference("Fournisseurs")
            // find fournisseur by name
            traderRef.orderByChild("name").equalTo(trader).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val fr = dataSnapshot.getValue(Fournisseur::class.java)
                    val myRef = database.getReference("Fournisseurs/" + fr!!.Id + "/versements")
                    // add versement to firebase -----------------------------------------------------
                    myRef.child(id).setValue(vrs)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }else{
            // find client by name
                val traderRef = database.getReference("Clients")
            // find client by name
            traderRef.orderByChild("name").equalTo(trader).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val cl = dataSnapshot.getValue(Client::class.java)
                    val myRef = database.getReference("Clients/" + cl!!.Id + "/versements")
                    // add versement to firebase -----------------------------------------------------
                    myRef.child(id).setValue(vrs)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
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

    private fun addTransaction(list_prod: MutableList<Product>, trader: String){
        // get values
        val date = Calendar.getInstance().time // it's unique (dateTime)
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        val desc: String =  v!!.trDesc.text.toString()


        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Transactions")

        if(trType.equals("Fournisseur")){
            // create new Entree
            var entree = Entree(desc, list_prod, date.toString(), trader)
            // add transaction to firebase
            myRef.child(date.toString()).setValue(entree)
        }else{
            // create new Sortie
            var sortie = Sortie(desc, list_prod, date.toString(), trader)
            // add transaction to firebase
            myRef.child(date.toString()).setValue(sortie)
        }

    }


}