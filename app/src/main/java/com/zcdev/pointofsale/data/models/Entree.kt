package com.zcdev.pointofsale.data.models

import java.util.ArrayList

class Entree: Transaction {


    constructor(desc:String, list_prod: MutableList<Product>, date:String, trader:String)
            :super(desc,list_prod,date, trader){
        this.type = "Entree"
        this.prixTotal = gettotalprice(list_prod)
    }
    constructor()

    fun gettotalprice(list_prod: MutableList<Product>): Double {
        var total:Double?=0.0

        for (p in list_prod){
            // prix achat
            total = total!! + (p.prixAchat!! * p.productQnt!!)
        }
        return total!!
    }

}
