package com.zcdev.pointofsale.data.models

open class Trader {

    // generate no-argument constructor by giving default values
    var Id:String?=null
    var Name: String?=null
    var Phone: String?=null
    var Email: String?=null
    var Address: String?=null


    constructor(Id:String,Name:String,Phone:String,Email:String,Address:String){
        this.Id = Id
        this.Name=Name
        this.Phone=Phone
        this.Email=Email
        this.Address=Address
    }

    constructor()   // **Add this**

}