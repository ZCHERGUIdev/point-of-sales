package com.zcdev.pointofsale.fragments.Products

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickCancel
import com.vansuita.pickimage.listeners.IPickResult
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_edit.view.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class EditFragment : Fragment() {
    var inputStream: InputStream? = null
    var imageUri: Uri?=null
    var imageLink:String?=null
    lateinit var postImage: ByteArray
    var progdialog: ProgressDialog? = null
    var v:View?=null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_edit, container, false)

        //receive data
        v!!.edtNameUpdate.setText(arguments?.getString("name"))
        v!!.edtBarcodeUpdate.setText(arguments?.getString("code"))
        v!!.edtDescUpdate.setText(arguments?.getString("desc"))
        v!!.edtQntUpdate.setText(arguments?.getString("qte"))

        Picasso.with(requireContext()).load(arguments?.getString("img")).into(v!!.ivPickImageUpdate)

        //Pick Image from galery
        v!!.ivPickImageUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                PickImageDialog.build(PickSetup()).setOnPickResult(object : IPickResult {
                    override fun onPickResult(r: PickResult?) {

                        v!!.ivPickImage.setImageURI(r!!.uri)
                        imageUri=r!!.uri
                        postImage= reduceImageSize(inputStream,r!!.uri)!!
                        Toast.makeText(requireContext(), "data"+postImage, Toast.LENGTH_SHORT).show()
                        if (imageUri!=null){
                            pushPhoto(postImage)
                        }

                    }

                }).setOnPickCancel(object : IPickCancel {
                    override fun onCancelClick() {
                        TODO("Not yet implemented")
                    }

                }).show(fragmentManager)
            }})

      // v!!.ivPickImageUpdate. ()

        return v
    }

    //enable options menu in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        //progressDialog setUp
        progdialog= ProgressDialog(requireContext())
        progdialog?.setMessage("Pleaze Wait...")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            // to do
            editProduct(v!!)
        }
        return super.onOptionsItemSelected(item)
    }



    fun editProduct(v:View){
        // get values

        val name: String =  v.edtNameUpdate.text.toString()
        val barcode: String = v.edtBarcodeUpdate.text.toString()
        val desc: String = v.edtDescUpdate.text.toString()
        val qnt: String = v.edtQntUpdate.text.toString()   // string to int --> Qte


        // add image !!
        var prd:Product
        if(imageLink==null)
        {
             prd = Product(name,barcode,desc,qnt,arguments?.getString("img")!!)
        }else{
             prd = Product(name,barcode,desc,qnt,imageLink!!)

        }


        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")

        // update product in firebase
        myRef.child(barcode).setValue(prd).addOnSuccessListener{

            // navigate to product list
            v!!.findNavController().navigate(R.id.action_editFragment_to_productsFragment)

            }.addOnFailureListener{

            Toast.makeText(v.context, "Sorry !! item not Updated", Toast.LENGTH_SHORT).show()
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