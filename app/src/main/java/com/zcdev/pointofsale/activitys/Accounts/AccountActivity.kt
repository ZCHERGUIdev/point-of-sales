package com.zcdev.pointofsale.activitys.Accounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.MainActivity
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {
    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        supportActionBar!!.hide()
        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()


    }

    fun login(view:View) {
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



}