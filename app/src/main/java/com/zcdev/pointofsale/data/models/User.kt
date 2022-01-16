package com.zcdev.pointofsale.data.models

class User {
    // generate no-argument constructor by giving default values
    var name: String?=null
    var email: String?=null
    var uid: String?=null


    constructor(name:String,email:String,uid:String){
        this.name=name
        this.email=email
        this.uid=uid
    }
    constructor()
}