
package com.zcdev.pointofsale.data.models

open class Transaction {

    // generate no-argument constructor by giving default values
    var Id:String?=null
    var desc: String?=" "
    var list_prod:MutableList<Product>?=null
    var date: String?=null
    var trader:String?=" "
    var type:String?=" "
    var qteProd: Int?=0
    var prixTotal:Double?=0.0


    constructor(desc:String,list_prod: MutableList<Product>,date:String, trader:String){
        this.Id = date
        this.desc=desc
        this.list_prod=list_prod
        this.date=date
        this.trader = trader
        this.qteProd= getqte(list_prod)
    }

    constructor()   // **Add this**



    fun getqte(list_prod: MutableList<Product>): Int {
        var qte:Int?=0
        for (p in list_prod){
            qte = qte!! + p.productQnt!!
        }
        return qte!!
    }

}