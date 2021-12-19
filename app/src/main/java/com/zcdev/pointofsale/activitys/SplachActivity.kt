package com.zcdev.pointofsale.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.zcdev.pointofsale.R
import com.zcdev.pointofsale.activitys.Accounts.AccountActivity

class SplachActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)
        supportActionBar!!.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,AccountActivity::class.java))
        },3000)

    }
}