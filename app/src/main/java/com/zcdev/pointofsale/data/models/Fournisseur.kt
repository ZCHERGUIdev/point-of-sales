package com.zcdev.pointofsale.data.models

class Fournisseur {

    // generate no-argument constructor by giving default values
    var frId:String?=null
    var frName: String?=null
    var frPhone: String?=null
    var frEmail: String?=null
    var frAddress: String?=null


    constructor(frId:String,frName:String,frPhone:String,frEmail:String,frAddress:String){
        this.frId = frId
        this.frName=frName
        this.frPhone=frPhone
        this.frEmail=frEmail
        this.frAddress=frAddress
    }
    constructor()   // **Add this**

}