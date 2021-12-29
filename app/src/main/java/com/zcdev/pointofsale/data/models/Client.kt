package com.zcdev.pointofsale.data.models

import android.os.Parcelable

class Client: Trader {

    // generate no-argument constructor by giving default values
    var reduction:String?="0"

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String, versements: HashMap<String,Versement>?, role:String, reduction:String)
            :super(Id,Name,Phone,Email,Address,versements,role){
        this.reduction = reduction
        if (versements!=null){
            this.sommeVrs = calculeSV(versements)
        }
    }

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String, reduction:String)
            :super(Id,Name,Phone,Email,Address){
        this.reduction = reduction
    }
    constructor()

    fun calculeSV(list_vrs: HashMap<String,Versement>): Double {
        var sum:Double?=0.0
        for ((key, value) in list_vrs) {
            sum = sum!! + value.montant!!
        }
        return sum!!
    }



}