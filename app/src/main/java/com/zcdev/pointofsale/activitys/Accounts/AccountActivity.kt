package com.zcdev.pointofsale.activitys.Accounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.MainActivity

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        supportActionBar!!.hide()

    }


    fun goTohome(view: View){
        startActivity(Intent(this.applicationContext, MainActivity::class.java))
    }
}