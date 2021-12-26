package com.zcdev.pointofsale.data.models


class Sortie: Transaction {

    // generate no-argument constructor by giving default values

    constructor(desc:String, list_prod: MutableList<Product>, date:String, trader:String)
            :super(desc,list_prod,date,trader){
        this.type = "Sortie"
        this.prixTotal = gettotalprice(list_prod)
    }
    constructor()
    fun gettotalprice(list_prod: MutableList<Product>): Double {
        var total:Double?=0.0

        for (p in list_prod){
            // prix vente
            total = total!! + (p.prixVente!! * p.productQnt!!)
        }
        return total!!
    }
     

}