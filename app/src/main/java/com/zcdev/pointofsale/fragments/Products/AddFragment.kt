package com.zcdev.pointofsale.fragments.Products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clientapp.UsersActivity.CaptureAct
import com.google.zxing.integration.android.IntentIntegrator
import com.zcdev.pointofsale.R
import kotlinx.android.synthetic.main.fragment_add.view.*

class AddFragment : Fragment() {
    var integrator:IntentIntegrator?=null

    companion object{
        var INSTANCE=AddFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v= inflater.inflate(R.layout.fragment_add, container, false)

        v.btnTovarBarcode.setOnClickListener {
            integrator= IntentIntegrator(this.requireActivity())
            integrator!!.setCaptureActivity(CaptureAct::class.java)
            integrator!!.setOrientationLocked(false)
            integrator!!.setDesiredBarcodeFormats()
            integrator!!.setPrompt("امسح الباركود الخاص بك !")
                .initiateScan()
        }

        return v
    }


}