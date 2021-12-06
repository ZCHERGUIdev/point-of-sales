package com.zcdev.pointofsale.fragments.Products

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.CaptureAct
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*



class AddFragment : Fragment() {
    var integrator:IntentIntegrator?=null
    private val PICK_IMAGE = 100
    var imageUri: Uri? = null
     var postImage: ByteArray?=null
    var v:View?=null

    companion object{
        var INSTANCE=AddFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add, container, false)
        // Set Menu
        setHasOptionsMenu(true)
        //qr code
        (v as View).btnTovarBarcode.setOnClickListener {
            integrator= IntentIntegrator(activity)
            integrator!!.setCaptureActivity(CaptureAct::class.java)
            integrator!!.setOrientationLocked(false)
            integrator!!.setDesiredBarcodeFormats()
            integrator!!.setPrompt("امسح الباركود الخاص بك !")
                .initiateScan()
        }
        //add image from galery
        //add image
        //add image
      v!!.ivPickImage.setOnClickListener(object : View.OnClickListener {
          override fun onClick(p0: View?) {
              // PickImageDialog.build(new PickSetup()).show(AddPostActivity.this);
              val intent = Intent()
              intent.type = "image/*"
              intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
              intent.action = Intent.ACTION_GET_CONTENT
              startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
          }

      })

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
                edtBarcode.setText(result.contents.toString())
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
                addProduct(v!!)
            // navigate to product list
            findNavController().navigate(R.id.action_addFragment_to_productsFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addProduct(v: View){
        // Inflate the layout for this fragment

        // get values
        val name: String =  v.edtName.text.toString()
        val barcode: String = v.edtBarcode.text.toString()
        val desc: String = v.edDesc.text.toString()
        val qnt: String = v.edtQnt.text.toString()   // string to int --> Qte

        // create new product
        var prd: Product = Product(name,barcode,desc,qnt,1);

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        // add product to firebase
        myRef.child(barcode).setValue(prd)
    }


}