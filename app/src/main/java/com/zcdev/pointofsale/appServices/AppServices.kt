package com.zcdev.pointofsale.appServices

import com.google.firebase.storage.FirebaseStorage


 class AppServices {

   // var db: FirebaseFirestore = FirebaseFirestore.getInstance()
  //  var mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    //push photo
    fun pushPhoto(imageFile: ByteArray?, pushPhotoCallback: PushPhotoCallback) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("store link")
        val childRef = storageRef.child(System.currentTimeMillis().toString() + "_dirasati.jpg")

        //uploading the image
        val uploadTask = childRef.putBytes(imageFile!!)
        uploadTask.addOnSuccessListener { //to get  image path ;
            val storageReference = FirebaseStorage.getInstance().getReference(childRef.path)
            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                val image_Path = downloadUrl.toString()
                pushPhotoCallback.onSuccess(image_Path)
            }
        }.addOnFailureListener { pushPhotoCallback.onFail() }
    }

}