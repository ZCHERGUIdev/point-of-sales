package com.zcdev.pointofsale.fragments.Documents

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Transaction
import com.zcdev.pointofsale.data.models.Document
import com.zcdev.pointofsale.fragments.Documents.Adapters.DocumentAdapter
import kotlinx.android.synthetic.main.doc_viewcell.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_doc.*
import kotlinx.android.synthetic.main.fragment_doc.view.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_products.view.*
import kotlinx.android.synthetic.main.fragment_versement.view.*
import java.util.*
import kotlin.collections.ArrayList

class DocumentsFragment : Fragment() {

    var list_doc: MutableList<Transaction> = ArrayList<Transaction>()
    var display_list: MutableList<Transaction> = ArrayList<Transaction>()

    //progressDialog
    var progdialog: ProgressDialog? = null


    companion object {
        var INSTANCE = DocumentsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //progressDialog setUp
        progdialog = ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")


        showDocuments()

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_doc, container, false)


        // Set Menu
        setHasOptionsMenu(true)


        val rvDocument: RecyclerView = view!!.rvDocument
        rvDocument.layoutManager = LinearLayoutManager(context)
        rvDocument.setHasFixedSize(true)

        view.addDocument.setOnClickListener {

            // alert dialogue
            // choose (entree /sortie )
            // redirect to (entree /sortie) fragment
            documentDialogue()
        }

        return view
    }


    private fun showDocuments() {

        //get products from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Transactions")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "transactions" root node
        progdialog!!.show()
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (trsnap in dataSnapshot.children) {
                    val tr = trsnap.getValue(Transaction::class.java) as Transaction
                    list_doc.add(tr)
                }
                display_list.addAll(list_doc)
                rvDocument.adapter = DocumentAdapter(activity!!, display_list)
                rvDocument.adapter!!.notifyDataSetChanged()

                checkData(display_list)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }


    private fun checkData(list: MutableList<Transaction>) {

        if (list.isEmpty()) {
            ivEmpty.visibility = View.VISIBLE
            tvEmpty.visibility = View.VISIBLE
            progdialog!!.hide()


        } else {
            ivEmpty.visibility = View.GONE
            tvEmpty.visibility = View.GONE
            progdialog!!.hide()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)
        val menuItem: MenuItem = menu.findItem(R.id.menu_search)

        if (menuItem != null) {

            var searchView: SearchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        display_list.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        list_doc.forEach {
                            if (it.trader!!.toLowerCase(Locale.getDefault()).contains(search)) {
                                display_list.add(it)
                            }
                        }

                        rvDocument.adapter!!.notifyDataSetChanged()
                    } else {
                        display_list.clear()
                        display_list.addAll(list_doc)
                        rvDocument.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }
            })
        }
    }

    private fun documentDialogue() {
        // Set up the checklist
        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice)
        arrayAdapter.add("Entree")
        arrayAdapter.add("Sortie")

        AlertDialog.Builder(context)
                .setTitle("add New Document")
                .setIcon(R.drawable.ic_main_menu_file)
                .setAdapter(arrayAdapter, DialogInterface.OnClickListener { dialog, which ->
                    val strName = arrayAdapter.getItem(which)
                    val builderInner = AlertDialog.Builder(context)
                    builderInner.setMessage(strName)
                    builderInner.setTitle("Your Selected Item is")
                    builderInner.setPositiveButton("Ok") { dialog, which ->

                        val bundle = bundleOf(
                                "tr" to strName!!.toLowerCase())
                        findNavController().navigate(R.id.action_documentsFragment_to_transactionFragment, bundle)

                        dialog.dismiss()
                    }
                    builderInner.show()
                })
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }


}