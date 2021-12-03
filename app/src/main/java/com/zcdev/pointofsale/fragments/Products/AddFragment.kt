package com.zcdev.pointofsale.fragments.Products

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.CaptureAct
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
        // Set Menu
        setHasOptionsMenu(true)
        //qr code
        v.btnTovarBarcode.setOnClickListener {
            integrator= IntentIntegrator(activity)
            integrator!!.setCaptureActivity(CaptureAct::class.java)
            integrator!!.setOrientationLocked(false)
            integrator!!.setDesiredBarcodeFormats()
            integrator!!.setPrompt("امسح الباركود الخاص بك !")
                .initiateScan()
        }

        return v
    }


// qr code result value
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                Toast.makeText(INSTANCE.requireContext(), "code :"+result.contents.toString(), Toast.LENGTH_SHORT).show()
                var intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(result.contents)
                )
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
        }
        return super.onOptionsItemSelected(item)
    }


}