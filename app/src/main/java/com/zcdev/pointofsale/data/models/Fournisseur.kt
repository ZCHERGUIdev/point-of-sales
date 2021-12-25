package com.zcdev.pointofsale.data.models

class Fournisseur: Trader {

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String, versements: HashMap<String,Versement>?, role:String)
            :super(Id,Name,Phone,Email,Address,versements,role){}

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String)
            :super(Id,Name,Phone,Email,Address){}
    constructor()

}