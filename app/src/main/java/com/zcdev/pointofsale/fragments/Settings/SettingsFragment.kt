package com.zcdev.pointofsale.fragments.Settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.zcdev.pointofsale.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var v= inflater.inflate(R.layout.fragment_settings, container, false)

       //support Multiple Language
        v.rlLang.setOnClickListener {
            languageDialogue()
        }
        v!!.general.setOnClickListener {
            Toast.makeText(requireContext(), "هذاالخيار معطل حاليا  !", Toast.LENGTH_SHORT).show()
        }
        v!!.about.setOnClickListener {
            Toast.makeText(requireContext(), "هذاالخيار معطل حاليا  !", Toast.LENGTH_SHORT).show()
        }
        v!!.share.setOnClickListener {
            Toast.makeText(requireContext(), "هذاالخيار معطل حاليا  !", Toast.LENGTH_SHORT).show()
        }



        return v
    }


    private fun languageDialogue() {
        // Set up the checklist
        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice)
        arrayAdapter.add("English")
        arrayAdapter.add("Arabic")

        AlertDialog.Builder(context)
            .setTitle("Choose the language")
            .setIcon(R.drawable.ic_language)
            .setAdapter(arrayAdapter, DialogInterface.OnClickListener { dialog, which ->
                val strName = arrayAdapter.getItem(which)
                val builderInner = AlertDialog.Builder(context)
                builderInner.setMessage(strName)
                builderInner.setTitle("Your Selected Item is")
                builderInner.setPositiveButton("Ok") { dialog, which ->

                   /* val bundle = bundleOf(
                        "tr" to strName!!.toLowerCase())
                    findNavController().navigate(R.id.action_documentsFragment_to_transactionFragment, bundle)*/

                    dialog.dismiss()
                }
                builderInner.show()
            })
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


}