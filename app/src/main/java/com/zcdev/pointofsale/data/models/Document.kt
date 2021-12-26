package com.zcdev.pointofsale.data.models

class Document {
    // generate no-argument constructor by giving default values
    var Id:String?=null
    var trader: String?=null
    var type: String?=null
    var date: String?=null
    var qteProd: Int?=0
    var prixTotal:Double?=0.0


    constructor(trader:String, type:String, date:String, qteProd:Int, prixTotal:Double){
        this.Id = date
        this.trader=trader
        this.type=type
        this.date=date
        this.qteProd=qteProd
        this.prixTotal = prixTotal
    }

    constructor()   // **Add this**
}