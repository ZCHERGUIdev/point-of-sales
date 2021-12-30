package com.zcdev.pointofsale.fragments.dashboard

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Client
import com.zcdev.pointofsale.data.models.Fournisseur
import com.zcdev.pointofsale.data.models.Product
import com.zcdev.pointofsale.data.models.Versement
import com.zcdev.pointofsale.fragments.Products.Adapters.ProductAdapter
import com.zcdev.pointofsale.fragments.Versements.Adapters.VersementAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_versement.*
import kotlinx.android.synthetic.main.fragment_versement.view.*
import kotlinx.android.synthetic.main.prod_viewcelll.view.*


class dashboardFragment : Fragment() {


    var stock:Double?=0.0
    var sp:Double?=0.0 // stock in sell price
    var fDebt:Double?=0.0
    var cDebt:Double?=0.0
    var nbprod:Int?=0
    var nbdocs:Int?=0
    var v:View?=null


    override fun onResume() {
        super.onResume()

        getStat()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        v= inflater.inflate(R.layout.fragment_dashboard, container, false)



        v!!.llMenuDocs.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_documentsFragment)
        }
        v!!.llProducts.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_productsFragment)
        }
        v!!.llMenuReports.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_reportsFragment)
        }
        v!!.llMenuExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_expensesFragment)
        }
        v!!.llMenuSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
        }
        v!!.llMenuhelp.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_helpFragment)
        }

        v!!.llMenucontact.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_contactFragment)
        }

        v!!.ivMenuforni.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_customersFragment)
        }

        v!!.ivMenuCustomer.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_clientFragment)
        }
        v!!.ivMenuEntree.setOnClickListener {
            val bundle = bundleOf(
                "tr" to "entree")
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionFragment, bundle)
        }
        v!!.ivMenuSorite.setOnClickListener {
            val bundle = bundleOf(
                    "tr" to "sortie")
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionFragment, bundle)
        }

        return v
    }

    private fun getStock(){
        //get products from firebase
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")
        var ttStockByProd = 0.0 // stock estimation
        var ttSPByProd = 0.0   // stock by sell price estimation

        //Get datasnapshot at your "products" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (prosnap in dataSnapshot.children) {
                    val prod = prosnap.getValue(Product::class.java)
                    ttStockByProd = ttStockByProd + prod!!.prixAchat * prod!!.productQnt!!
                    ttSPByProd = ttSPByProd + prod!!.prixVente * prod!!.productQnt!!
                }
                nbprod = dataSnapshot.childrenCount.toInt()
                stock = ttStockByProd
                sp = ttSPByProd
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }

    private fun getDebt() {

        var debtCL:Double?=0.0
        var debtFR:Double?=0.0

            //get fournisseurs/clients from firebase
            val database = FirebaseDatabase.getInstance()
            val frRef = database.getReference("Fournisseurs")
            val clRef = database.getReference("Clients")


            //Get datasnapshot at your "Fournisseurs" root node
            val eventListenerFR: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of users in datasnapshot

                    for (frsnap in dataSnapshot.children) {
                        val fr = frsnap.getValue(Fournisseur::class.java)
                        debtFR = debtFR!! + fr!!.balance!!
                    }
                    fDebt = debtFR
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            //Get datasnapshot at your "Clients" root node
            val eventListenerCL: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of users in datasnapshot

                    for (clsnap in dataSnapshot.children) {
                        val cl = clsnap.getValue(Client::class.java)
                        debtCL = debtCL!! + cl!!.balance!!
                    }
                    cDebt = debtCL
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }

            frRef.addListenerForSingleValueEvent(eventListenerFR)
            clRef.addListenerForSingleValueEvent(eventListenerCL)


        }

    private fun getDocs(){
        //get docs from firebase
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Transactions")


        //Get datasnapshot at your "transactions" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                nbdocs = dataSnapshot.childrenCount.toInt()

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }

    private fun getStat(){
        getStock()
        getDebt()
        getDocs()

        v!!.tvStock.setText(stock.toString())
        v!!.tvSP.setText(sp.toString())
        v!!.tvCreditF.setText(fDebt.toString())
        v!!.tvCreditC.setText(cDebt.toString())
        v!!.tvnbDoc.setText(nbdocs.toString())
        v!!.tvnbPrd.setText(nbprod.toString())
    }

    }