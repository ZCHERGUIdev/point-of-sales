package com.zcdev.pointofsale.fragments.Settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zcdev.pointofsale.R


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var v= inflater.inflate(R.layout.fragment_settings, container, false)





        return v
    }


}