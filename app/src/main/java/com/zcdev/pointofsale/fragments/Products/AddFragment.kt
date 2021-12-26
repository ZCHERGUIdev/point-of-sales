package com.zcdev.pointofsale.fragments.Products

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickCancel
import com.vansuita.pickimage.listeners.IPickResult
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.CaptureAct
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_add.view.btnTovarBarcode
import kotlinx.android.synthetic.main.fragment_edit.view.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class AddFragment : Fragment(){

    var integrator: IntentIntegrator? = null
    var inputStream: InputStream? = null
    var imageUri:Uri?=null
    var imageLink:String?=null
    lateinit var postImage: ByteArray
    var progdialog: ProgressDialog? = null



    var v:View?=null

    companion object{
        var INSTANCE=AddFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")
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
         v!!.btnTovarBarcode.setOnClickListener {
           initQrCode()
         }
        //Pick Image from galery
        v!!.ivPickImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
               PickImageDialog.build(PickSetup()).setOnPickResult(object :IPickResult{
                   override fun onPickResult(r: PickResult?) {

                       v!!.ivPickImage.setImageURI(r!!.uri)
                       imageUri=r!!.uri
                       postImage= reduceImageSize(inputStream,r!!.uri)!!
                       Toast.makeText(requireContext(), "data"+postImage, Toast.LENGTH_SHORT).show()
                       if (imageUri!=null){
                           pushPhoto(postImage)
                       }

                   }

               }).setOnPickCancel(object :IPickCancel{
                   override fun onCancelClick() {
                       TODO("Not yet implemented")
                   }

               }).show(fragmentManager)
            }})
        return v
    }

   private fun initQrCode() {
        integrator= IntentIntegrator(activity)
        integrator!!.setCaptureActivity(CaptureAct::class.java)
        integrator!!.setOrientationLocked(false)
        integrator!!.setDesiredBarcodeFormats()
        integrator!!.setPrompt("امسح الباركود الخاص بك !")
            .initiateScan()
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
        val qnt: Int = v.edtQnt.text.toString().toInt()
        val prixAchat: Double = v.edtPrixA.text.toString().toDouble()
        val prixVente: Double = v.edtPrixV.text.toString().toDouble()



        // create new product
        var prd: Product = Product(name,barcode,desc,qnt,prixAchat,prixVente,imageLink!!)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        // add product to firebase
        myRef.child(barcode).setValue(prd)
    }









    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                Toast.makeText(requireContext(), "code :"+result.contents, Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun reduceImageSize(inputStream: InputStream?, imageUri: Uri?): ByteArray? {
        var inputStream = inputStream
        try {
            inputStream = requireContext().getContentResolver().openInputStream(imageUri!!)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val selectedImage = BitmapFactory.decodeStream(inputStream)

        //----  to reduce image size  -----------------------
        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        return baos.toByteArray()
        //----  to reduce image size  ---
    }

    //push photo and get link
    fun pushPhoto(imageFile: ByteArray?) :String{
       progdialog!!.show()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://gstock-6e8e2.appspot.com/imagesprod")
        val childRef = storageRef.child(System.currentTimeMillis().toString() + "_gstockprod.jpg")

        //uploading the image
        val uploadTask = childRef.putBytes(imageFile!!)
        uploadTask.addOnSuccessListener { //to get  image path ;
            val storageReference = FirebaseStorage.getInstance().getReference(childRef.path)
            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                 imageLink = downloadUrl.toString()
               // Toast.makeText(requireContext(), "link"+imageLink.toString(), Toast.LENGTH_SHORT).show()
                progdialog!!.hide()
            }
        }.addOnFailureListener {}
        return imageLink.toString()
    }

}







