package com.zcdev.pointofsale.fragments.dashboard

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Client
import com.zcdev.pointofsale.data.models.Fournisseur
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_versement.*
import kotlinx.android.synthetic.main.fragment_versement.view.*
import kotlinx.android.synthetic.main.prod_viewcelll.view.*


class dashboardFragment : Fragment() {
    //progressDialog
    var progdialog: ProgressDialog? = null
 //   var  mSwipeRefreshLayout : SwipeRefreshLayout?=null

    var stock:Double?=0.0
    var sp:Double?=0.0 // stock in sell price
    var fDebt:Double?=0.0
    var cDebt:Double?=0.0
    var nbprod:Int?=0
    var nbdocs:Int?=0
    lateinit var auth: FirebaseAuth


    var v:View?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //progressDialog setUp
        progdialog = ProgressDialog(this.requireContext())
        progdialog?.setMessage("Pleaze Wait...")
        Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
       // mSwipeRefreshLayout!!.setOnRefreshListener(this)
        auth = FirebaseAuth.getInstance()

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

       var v= inflater.inflate(R.layout.fragment_dashboard, container, false)
      // mSwipeRefreshLayout= v.splayout as SwipeRefreshLayout?




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
            Toast.makeText(requireContext(), "لم يتم إيضافة هذه الخاصية بعد !", Toast.LENGTH_SHORT).show()

        }
        v!!.llMenuFeedback.setOnClickListener {
           // findNavController().navigate(R.id.action_dashboardFragment_to_expensesFragment)
            Toast.makeText(requireContext(), "هذاالخيار معطل حاليا  !", Toast.LENGTH_SHORT).show()

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


        progdialog!!.show()
        getStock(v)
        getDebt(v)
        getDocs(v)


        return v
    }

    private fun getStock(v: View){
        //get products from firebase
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Products")
        var ttStockByProd = 0.0 // stock estimation
        var ttSPByProd = 0.0   // stock by sell price estimation
        progdialog?.show()
        //Get datasnapshot at your "products" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (prosnap in dataSnapshot.children) {
                    val prod = prosnap.getValue(Product::class.java)
                    ttStockByProd = ttStockByProd + prod!!.prixAchat * prod!!.productQnt!!
                    ttSPByProd = ttSPByProd + prod!!.prixVente * prod!!.productQnt!!
                }
                nbprod = dataSnapshot.childrenCount.toInt()
                v.tvnbPrd.setText(nbprod.toString())

                stock = ttStockByProd
                progdialog!!.hide()
                v.tvStock.setText(stock.toString())
                sp = ttSPByProd
                v.tvSP.setText(sp.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }

    private fun getDebt(v: View) {

        var debtCL:Double?=0.0
        var debtFR:Double?=0.0

            //get fournisseurs/clients from firebase
            val currentUser =auth.currentUser
            val database = FirebaseDatabase.getInstance()
            val frRef = database.getReference("Users/"+ currentUser!!.uid +"/Fournisseurs")
            val clRef = database.getReference("Users/"+ currentUser!!.uid +"/Clients")
             progdialog?.show()

            //Get datasnapshot at your "Fournisseurs" root node
            val eventListenerFR: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of users in datasnapshot

                    for (frsnap in dataSnapshot.children) {
                        val fr = frsnap.getValue(Fournisseur::class.java)
                        debtFR = debtFR!! + fr!!.balance!!
                    }
                    fDebt = debtFR
                    v.tvCreditF.setText(fDebt.toString())
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
                    v.tvCreditC.setText(cDebt.toString())


                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }

            frRef.addListenerForSingleValueEvent(eventListenerFR)
            clRef.addListenerForSingleValueEvent(eventListenerCL)


        }

    private fun getDocs(v: View){
        //get docs from firebase
        val currentUser =auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/"+ currentUser!!.uid +"/Transactions")

        progdialog?.show()
        //Get datasnapshot at your "transactions" root node
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                nbdocs = dataSnapshot.childrenCount.toInt()
                v.tvnbDoc.setText(nbdocs.toString())


            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }

   /* override fun onRefresh() {
        Toast.makeText(requireContext(), "Refresh", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { mSwipeRefreshLayout!!.isRefreshing = false }, 2000)
    }*/


}