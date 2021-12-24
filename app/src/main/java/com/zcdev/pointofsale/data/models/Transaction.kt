
package com.zcdev.pointofsale.data.models

import java.util.ArrayList

open class Transaction {

    // generate no-argument constructor by giving default values
    var Id:String?=null
    var desc: String?=" "
    var list_prod:MutableList<Product>?=null
    var date: String?=null


    constructor(Id:String,desc:String,list_prod: MutableList<Product>,date:String){
        this.Id = Id
        this.desc=desc
        this.list_prod=list_prod
        this.date=date
    }

    constructor()   // **Add this**

}