package com.zcdev.pointofsale.fragments.Settings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.zcdev.pointofsale.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.util.*


class SettingsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocal()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var v= inflater.inflate(R.layout.fragment_settings, container, false)
       //support Multiple Language
        v.rlLang.setOnClickListener {
          //  languageDialogue()
            showChangeLanguageDialog()
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


    /*private fun languageDialogue() {
        // Set up the checklist
        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice)
        arrayAdapter.add("English")
        arrayAdapter.add("العربية")

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
    }*/

    private fun showChangeLanguageDialog(){
        // Set up the checklist
        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice)
        arrayAdapter.add("Frensh")
        arrayAdapter.add("العربية")
        arrayAdapter.add("English")



        AlertDialog.Builder(context)
            .setTitle("Choose the language")
            .setSingleChoiceItems(arrayAdapter,-1,object :DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (p1==0){
                        // French
                        setLocal("fr")
                       requireActivity().recreate()
                    }
                    else if (p1==1){
                        // Arabic
                        setLocal("ar")
                        requireActivity().recreate()
                    }
                    else if (p1==2){
                        // Arabic
                        setLocal("en")
                        requireActivity().recreate()
                    }

                    //dimisss alert dialog when language selected
                    p0!!.dismiss()
                }
            }).show()
    }

    private fun setLocal(lang:String){
     val local=Locale(lang)
        Locale.setDefault(local)
        val conf=Configuration()
        conf.locale=local
        requireContext().resources.updateConfiguration(conf,requireContext().resources.displayMetrics)
        //save data shared preferences
        val preferences = requireActivity()!!.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        preferences.putString("My_Lang",lang)
        preferences.apply()

    }
// load language

    private fun loadLocal(){
        val preferences = requireActivity()!!.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lang=preferences.getString("My_Lang","")
        setLocal(lang!!)

    }

}