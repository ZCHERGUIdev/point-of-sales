package com.zcdev.pointofsale.data.models

class Product {

    var productName:String?=null
    var productCode:String?=null
    var productDesc:String?=null
    var productQnt:String?=null
    //var productPrice:Float?=null
    var productImg:Int?=null


    constructor(name:String,code:String,desc:String,qnt:String,img:Int){
        this.productName=name
        this.productCode=code
        this.productDesc=desc
        this.productQnt=qnt
        //this.productPrice=price
        this.productImg=img
    }
}