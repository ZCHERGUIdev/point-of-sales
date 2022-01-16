package com.zcdev.pointofsale.activitys.Accounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.MainActivity
import com.zcdev.pointofsale.data.models.Product
import com.zcdev.pointofsale.data.models.User
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.fragment_add.view.*

class AccountActivity : AppCompatActivity() {
    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        supportActionBar!!.hide()
        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()
        if (checkLoggedIn()){
            writeUserData()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    fun login(view: View) {
        val email = textEmail.text.toString()
        val pass = textPassword.text.toString()
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                startActivity(Intent(this.applicationContext, MainActivity::class.java))
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }
    fun signUp(view:View) {
        Toast.makeText(this, "للحصول على حساب فعال قم بلإشتراك معنا ", Toast.LENGTH_SHORT).show()
    }

    fun checkLoggedIn() :Boolean{
        if (auth.currentUser!=null){
            Log.i("Appp"," usr${auth.currentUser} signed in succesfully")
            return true
        }else{
            Log.i("Appp","usr is not signin")
            return false
        }
        return false
    }

    fun writeUserData() {
        var currentUser =auth.currentUser

        val name:String? = currentUser!!.displayName
        val email:String? = currentUser!!.email


        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getToken() instead.
        val uid:String? = currentUser.uid

        // create new user
        var user = User(name!!,email!!,uid!!)

        // get fireabse database instance
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users").child(currentUser!!.uid)


        // add product to firebase
        myRef.child("uid").setValue(user.uid!!)
        myRef.child("email").setValue(user.email!!)
    }




}