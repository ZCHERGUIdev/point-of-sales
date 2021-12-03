package com.zcdev.pointofsale.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.database.FirebaseDatabase
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.data.models.Product


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)


        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")
        var id = "1";
        myRef.child(id).setValue("lait soummam")
        id = "2";
        myRef.child(id).setValue("Lait candiat")
        id = "3";
        myRef.child(id).setValue("Yago")
        id = "4";
        myRef.child(id).setValue("fromage")
        id = "5";
        myRef.child(id).setValue("fromage rouge")
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}