package com.zcdev.pointofsale.data.models

class Product {

    // generate no-argument constructor by giving default values
    var productName: String?=null
    var productCode: String?=null
    var productDesc: String?=null
    var productQnt: Int?=0
    var prixAchat: Double =0.0
    var prixVente: Double =0.0
    var productImg: String?=null


    constructor(name:String,code:String,desc:String,qnt:Int,prixAchat:Double,prixVente:Double,img:String){
        this.productName=name
        this.productCode=code
        this.productDesc=desc
        this.productQnt=qnt
        this.prixAchat=prixAchat
        this.prixVente=prixVente
        this.productImg=img
    }

    constructor(name:String,code:String,desc:String,qnt:Int,prixAchat:Double,prixVente:Double){
        this.productName=name
        this.productCode=code
        this.productDesc=desc
        this.productQnt=qnt
        this.prixAchat=prixAchat
        this.prixVente=prixVente
    }
    constructor()   // **Add this**

}