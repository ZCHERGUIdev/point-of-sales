package com.zcdev.pointofsale.data.models

open class Trader {

    // generate no-argument constructor by giving default values
    var Id:String?=null
    var Name: String?=null
    var Phone: String?=null
    var Email: String?=null
    var Address: String?=null
    var versements:HashMap<String,Versement>?=null
    var role:String?=" "
    var sommeVrs:Int?=0


    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String, versements: HashMap<String,Versement>?, role:String){
        this.Id = Id
        this.Name=Name
        this.Phone=Phone
        this.Email=Email
        this.Address=Address
        this.versements = versements
        this.role = role
    }

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String){
        this.Id = Id
        this.Name=Name
        this.Phone=Phone
        this.Email=Email
        this.Address=Address
    }

    constructor()   // **Add this**



}