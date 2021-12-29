package com.zcdev.pointofsale.data.models

open class Versement {

    // generate no-argument constructor by giving default values
    var Id:String?=null
    var date: String?=null
    var montant: Double?=0.0


    constructor(Id:String,date:String, montant:Double){
        this.Id = Id
        this.date=date
        this.montant = montant
    }

    constructor()   // **Add this**

}