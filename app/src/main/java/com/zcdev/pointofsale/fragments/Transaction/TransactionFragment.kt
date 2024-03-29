package com.zcdev.pointofsale.fragments.Transaction

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uttampanchasara.pdfgenerator.CreatePdf
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.*
import com.zcdev.pointofsale.fragments.Fournisseur.Adapters.TransactionAdapter
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.android.synthetic.main.fragment_transaction.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TransactionFragment : Fragment() {
    private val ACCESS_EXTERNAL_STORAGE_PERMISSION_ID = 222
    var permissionCheckReadExternalStorage = PackageManager.PERMISSION_DENIED
    var permissionCheckWriteExternalStorage = PackageManager.PERMISSION_DENIED

    var v:View?=null
    var trType:String?=""
    var totalPrice:String?=""
    var arrayAdapter:ArrayAdapter<String>? =null
    var display_list: MutableList<Product> = ArrayList<Product>()
    var ttprice:Double?=0.0
    lateinit var auth: FirebaseAuth

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        if (arguments?.getString("tr").equals("entree")){
            trType = "Fournisseur"
            arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
                    getFourniseurs())
            if (arguments?.getSerializable("prds")!=null){
                display_list = arguments?.getSerializable("prds") as ArrayList<Product>
                var entree = Entree()
                ttprice = entree.gettotalprice(display_list)
            }

        }else{
            trType = "Client"
            arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
                    getClients())
            if (arguments?.getSerializable("prds")!=null){
                display_list = arguments?.getSerializable("prds") as ArrayList<Product>
                var sortie = Sortie()
                ttprice = sortie.gettotalprice(display_list)
            }
        }
        v!!.totalprice.setText(ttprice.toString())


        rvProducts.adapter = TransactionAdapter(requireActivity(), display_list, trType!!)
        rvProducts.adapter!!.notifyDataSetChanged()


        v!!.autoCompleteTextView.setText(trType, false)
        v!!.autoCompleteTextView.setAdapter(arrayAdapter)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestStoragePermissions()
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
         totalPrice=v!!.totalprice.text.toString()
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
        }else if (item.itemId == R.id.iprint){
            Toast.makeText(this.context, "done ", Toast.LENGTH_SHORT).show()
            doPrint()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doPrint() {
        var num=(0..1000).random()
        CreatePdf(requireContext())
            .setPdfName("Ticket"+num.toString())
            .setContentBaseUrl("")
            .setPageSize(PrintAttributes.MediaSize.ISO_A6)
            .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/Tickets")
            .setContent(
           """" <center class="ticket">
        <img
         src="https://firebasestorage.googleapis.com/v0/b/gstock-6e8e2.appspot.com/o/logo.png?alt=media&token=7270e439-f710-4436-94a0-6e72abb3181e" alt="Logo"  width="90" 
     height="90">
       <table>
        <thead>
        <tr>
        <th class="quantity">Q.</th>
        <th class="description">Description</th>
        <th class="price">DA</th>
        </tr>
        </thead>
        <tbody>
        <tr>
        <td class="quantity">${display_list[0].productQnt}</td>
        <td class="description">${display_list[0].productDesc}</td>
        <td class="price">${display_list[0].prixVente} DA</td>
        </tr>
        <tr>
        <td class="quantity">${display_list.size}</td>
        <td class="description">TOTAL</td>
        <td class="price">${totalprice.text} DZD</td>
        </tr>
        </tbody>
        </table>
        <p class="centered">Thanks ${autoCompleteTextView.text} for your purchase!
        <br>Stock Genius</p>
        </center>"""
            )
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(filePath: String) {
                    Toast.makeText(requireContext(), "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
                }
            })
            .create()
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
                    addVersement(input.text.toString().toDouble(), trader)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }
    private fun addVersement(montant: Double, trader: String){


        // get values
        val date = Calendar.getInstance().time // it's unique (dateTime)
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        // get fireabse database instance
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val id:String= date.toString()
        var balance:Double?
        // create new versement
        var vrs = Versement(id, formatedDate.toString(), montant)

        if(trType.equals("Fournisseur")){
            val traderRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs")
            // find fournisseur by name
            traderRef.orderByChild("name").equalTo(trader).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val fr = dataSnapshot.getValue(Fournisseur::class.java)
                    val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs/" + fr!!.Id + "/versements")
                    // add versement to firebase -----------------------------------------------------
                    myRef.child(id).setValue(vrs)

                    // calculate balance
                    balance  = fr!!.balance!! + vrs.montant!!

                    val frRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs/" + fr!!.Id)
                    //// update balance of trader -----------------------------------------------------
                    frRef.child("balance").setValue(balance)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        }else{
            // find client by name
                val traderRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients")
            // find client by name
            traderRef.orderByChild("name").equalTo(trader).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val cl = dataSnapshot.getValue(Client::class.java)
                    val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients/" + cl!!.Id + "/versements")
                    // add versement to firebase -----------------------------------------------------
                    myRef.child(id).setValue(vrs)

                    // calculate balance
                    balance  = cl!!.balance!! + vrs.montant!!

                    val clRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients/" + cl!!.Id)
                    //// update balance of trader -----------------------------------------------------
                    clRef.child("balance").setValue(balance)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun getFourniseurs():MutableList<String>{
        var list_fr: MutableList<String> = ArrayList<String>()

        //get fournisseurs from firebase
        // read from db firebase ------------------------------------------------------------------
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs")

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
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients")

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
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("Users/"+ currentUser!!.uid +"/Transactions")

        if(trType.equals("Fournisseur")){
            // create new Entree
            val entree = Entree(desc, list_prod, date.toString(), trader)
            // add transaction to firebase
            myRef.child(date.toString()).setValue(entree)

            val traderRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs")
            // find fournisseur by name
            traderRef.orderByChild("name").equalTo(trader).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val fr = dataSnapshot.getValue(Fournisseur::class.java)
                    // calculate balance
                    var balance:Double  = fr!!.balance!! - entree.prixTotal!!

                    val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs/" + fr!!.Id)
                    //// update balance of trader -----------------------------------------------------
                    myRef.child("balance").setValue(balance)
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
            // create new Sortie
            val sortie = Sortie(desc, list_prod, date.toString(), trader)
            // add transaction to firebase
            myRef.child(date.toString()).setValue(sortie)

            val traderRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients")
            // find fournisseur by name
            traderRef.orderByChild("name").equalTo(trader).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val cl = dataSnapshot.getValue(Client::class.java)
                    // calculate balance
                    var balance:Double  = cl!!.balance!! - sortie.prixTotal!!

                    val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients/" + cl!!.Id)

                    //// update balance of trader -----------------------------------------------------
                    myRef.child("balance").setValue(balance)

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

        // update Qte products
        // get fireabse database instance
        myRef = database.getReference("Users/"+ currentUser!!.uid +"/Products")
        for(p in list_prod){

            // find p by barcode
            myRef.orderByChild("productCode").equalTo(p.productCode).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val prd = dataSnapshot.getValue(Product::class.java)
                    val qte:Int
                    if(trType.equals("Fournisseur")){
                        qte = prd!!.productQnt!! + p.productQnt!!
                    }else{
                        qte = prd!!.productQnt!! - p.productQnt!!
                    }

                    val prdRef = database.getReference("Users/"+ currentUser!!.uid +"/Products/"+prd.productCode)
                    prdRef.child("productQnt").setValue(qte)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }



    private fun requestStoragePermissions() {
        permissionCheckWriteExternalStorage =
            ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionCheckReadExternalStorage =
            ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED
            || permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please provide permission to access the external storage... I need to save the contacts photos",
                    Toast.LENGTH_LONG
                ).show()
            }
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                ACCESS_EXTERNAL_STORAGE_PERMISSION_ID
            )
        }
    }


}