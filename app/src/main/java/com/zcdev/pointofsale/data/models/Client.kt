package com.zcdev.pointofsale.data.models

class Client: Trader {

    // generate no-argument constructor by giving default values
    var reduction:String?="0"

    constructor(Id:String,Name:String,Phone:String,Email:String,Address:String,reduction:String):super(Id,Name,Phone,Email,Address){
        this.reduction = reduction
    }
    constructor()
     

}