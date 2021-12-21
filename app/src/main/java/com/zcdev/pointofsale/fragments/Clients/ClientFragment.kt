package com.zcdev.pointofsale.fragments.Clients

import android.app.ProgressDialog
import android.content.Context
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
import com.zcdev.pointofsale.data.models.Client
import com.zcdev.pointofsale.fragments.Clients.Adapters.ClientAdapter
import kotlinx.android.synthetic.main.fragment_client.*
import kotlinx.android.synthetic.main.fragment_client.view.*
import java.util.*


class ClientFragment : Fragment() {


    var list_cl: MutableList<Client> = ArrayList<Client>()
    var display_list: MutableList<Client> = ArrayList<Client>()
    //progressDialog
    var progdialog: ProgressDialog? = null

    companion object{
        var INSTANCE= ClientFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")


        showClient()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=  inflater.inflate(R.layout.fragment_client, container, false)

        // Set Menu
        setHasOptionsMenu(true)

        setRecyclerFr(view, requireContext())
        return view
    }





    private fun showClient(){
        var clients = ArrayList<Client>()

        //get clinets from firebase
        // read from db firebase ------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Clients")

        //First retrieve your users datasnapshot.
        //Get datasnapshot at your "Clients" root node
        progdialog!!.show()
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get map of users in datasnapshot

                for (clsnap in dataSnapshot.children) {
                    val cl = clsnap.getValue(Client::class.java)
                    list_cl.add(cl!!)
                }
                display_list.addAll(list_cl)
                rvClient.adapter = ClientAdapter(activity!!, display_list)
                rvClient.adapter!!.notifyDataSetChanged()

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
                        list_cl.forEach {
                            if (it.Name!!.toLowerCase(Locale.getDefault()).contains(search)){
                                display_list.add(it)
                            }
                        }

                        rvClient.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        display_list.clear()
                        display_list.addAll(list_cl)
                        rvClient.adapter!!.notifyDataSetChanged()
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

    fun setRecyclerFr(v:View, c: Context):RecyclerView{

        val rvClient: RecyclerView = v.rvClient

        rvClient.layoutManager = LinearLayoutManager(c)
        rvClient.setHasFixedSize(true)
        v.addClients.setOnClickListener{
            v!!.findNavController().navigate(R.id.action_clientFragment_to_addFragmentCl)
        }
        return rvClient
    }



}