package com.zcdev.pointofsale.data.models

class Product {

    // generate no-argument constructor by giving default values
    var productName: String?=null
    var productCode: String?=null
    var productDesc: String?=null
    var productQnt: String?=null
    //var productPrice:Float?=null
    var productImg: String?=null


    constructor(name:String,code:String,desc:String,qnt:String,img:String){
        this.productName=name
        this.productCode=code
        this.productDesc=desc
        this.productQnt=qnt
        //this.productPrice=price
        this.productImg=img
    }

    constructor(name:String,code:String,desc:String,qnt:String){
        this.productName=name
        this.productCode=code
        this.productDesc=desc
        this.productQnt=qnt
        //this.productPrice=price
    }
    constructor()   // **Add this**

}