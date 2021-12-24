package com.zcdev.pointofsale.data.models

import java.util.ArrayList

class Entree: Transaction {

    var type:String?=" "
    var fournisseur:String?=" "

    constructor(Id:String, desc:String, list_prod: MutableList<Product>, date:String, fournisseur:String):super(Id,desc,list_prod,date){
        this.type = "Entree"
        this.fournisseur = fournisseur
    }
    constructor()

}
