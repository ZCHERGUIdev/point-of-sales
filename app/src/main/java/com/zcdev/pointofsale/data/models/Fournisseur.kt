package com.zcdev.pointofsale.data.models

class Fournisseur: Trader {

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String, versements: HashMap<String,Versement>?, role:String)
            :super(Id,Name,Phone,Email,Address,versements,role){
        if (versements!=null){
            this.sommeVrs = calculeSV(versements)
        }
            }

    constructor(Id:String, Name:String, Phone:String, Email:String, Address:String)
            :super(Id,Name,Phone,Email,Address){}
    constructor()

    fun calculeSV(list_vrs: HashMap<String,Versement>): Int {
        var sum:Int?=0
        for ((key, value) in list_vrs) {
            sum = sum!! + value.montant!!
        }
        return sum!!
    }

}