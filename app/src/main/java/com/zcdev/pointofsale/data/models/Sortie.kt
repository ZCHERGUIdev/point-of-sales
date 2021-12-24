package com.zcdev.pointofsale.data.models

import java.util.ArrayList

class Sortie: Transaction {

    // generate no-argument constructor by giving default values
    var type:String?=" "
    var client:String?=" "

    constructor(Id:String, desc:String, list_prod: MutableList<Product>, date:String, client:String):super(Id,desc,list_prod,date){
        this.type = "Sortie"
        this.client = client
    }
    constructor()
     

}