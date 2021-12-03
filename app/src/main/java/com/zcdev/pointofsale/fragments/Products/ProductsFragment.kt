package com.zcdev.pointofsale.fragments.Products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.zcdev.pointofsale.R
import kotlinx.android.synthetic.main.fragment_products.view.*


class ProductsFragment : Fragment() {
    var integrator:IntentIntegrator?=null

    companion object{
        var INSTANCE=ProductsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_products, container, false)

        view.addProd.setOnClickListener{
            findNavController().navigate(R.id.action_productsFragment_to_addFragment)
        }

        return view
    }


}