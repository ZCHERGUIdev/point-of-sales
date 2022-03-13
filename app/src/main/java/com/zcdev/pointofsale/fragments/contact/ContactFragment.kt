package com.zcdev.pointofsale.fragments.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zcdev.pointofsale.R
import kotlinx.android.synthetic.main.fragment_contact.view.*


class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var v= inflater.inflate(R.layout.fragment_contact, container, false)
        v.facebookImageView.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Stock-Genius-102422035737362/"))
            startActivity(intent)
        }
        v.phoneImageView.setOnClickListener {
            var intent =Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "0555606475"))
            startActivity(intent)
        }

        return v
    }


}