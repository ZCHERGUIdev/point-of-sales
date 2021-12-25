package com.zcdev.pointofsale.fragments.Fournisseur

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Fournisseur
import com.zcdev.pointofsale.fragments.Fournisseur.Adapters.FournisseurAdapter
import kotlinx.android.synthetic.main.fragment_fournisseur.*
import kotlinx.android.synthetic.main.fragment_fournisseur.view.*
import java.util.*


class FournisseurFragment : Fragment() {
    var list_fr: MutableList<Fournisseur> = ArrayList<Fournisseur>()
    var display_list: MutableList<Fournisseur> = ArrayList<Fournisseur>()

    //progressDialog
    var progdialog: ProgressDialog? = null

    companion object{
        var INSTANCE= FournisseurFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")


        showFourniseurs()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=  inflater.inflate(R.layout.fragment_fournisseur, container, false)
        // Set Menu
        setHasOptionsMenu(true)
        val rvFournisseur: RecyclerView = view.rvFournisseur



        rvFournisseur.layoutManager = LinearLayoutManager(context)
        rvFournisseur.setHasFixedSize(true)
        view.addFournisseur.setOnClickListener{
            view!!.findNavController().navigate(R.id.action_fournisseurFragment_to_addFragmentFr)
        }

        return view
    }





    private fun showFourniseurs(){
        var fournisseurs = ArrayList<Fournisseur>()

        //get fournisseurs from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Fournisseurs")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "Fournisseurs" root node
        progdialog!!.show()
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (frsnap in dataSnapshot.children) {
                    val fr = frsnap.getValue(Fournisseur::class.java)
                    list_fr.add(fr!!)
                }
                display_list.addAll(list_fr)
                rvFournisseur.adapter = FournisseurAdapter(activity!!, display_list)
                rvFournisseur.adapter!!.notifyDataSetChanged()

                progdialog!!.hide()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRef.addListenerForSingleValueEvent(eventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)
        val menuItem: MenuItem = menu.findItem(R.id.menu_search)

        if (menuItem != null){

            var searchView: SearchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        display_list.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        list_fr.forEach {
                            if (it.Name!!.toLowerCase(Locale.getDefault()).contains(search)){
                                display_list.add(it)
                            }
                        }

                        rvFournisseur.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        display_list.clear()
                        display_list.addAll(list_fr)
                        rvFournisseur.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
        }
        return super.onOptionsItemSelected(item)
    }


}