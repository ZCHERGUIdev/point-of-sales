package com.zcdev.pointofsale.fragments.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_dashboard.view.*


class dashboardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var v= inflater.inflate(R.layout.fragment_dashboard, container, false)
        v.llMenuDocs.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_documentsFragment)
        }
        v.llProducts.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_productsFragment)
        }
        v.llMenuReports.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_reportsFragment)
        }
        v.llMenuExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_expensesFragment)
        }
        v.llMenuSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
        }
        v.llMenuhelp.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_helpFragment)
        }

        v.llMenucontact.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_contactFragment)
        }

        v.ivMenuforni.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_customersFragment)
        }

        v.ivMenuCustomer.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_clientFragment)
        }
        v.ivMenuEntree.setOnClickListener {
            val bundle = bundleOf(
                "tr" to "entree")
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionFragment, bundle)
        }
        v.ivMenuSorite.setOnClickListener {
            val bundle = bundleOf(
                "tr" to "sortie")
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionFragment, bundle)
        }



        return v
    }







}