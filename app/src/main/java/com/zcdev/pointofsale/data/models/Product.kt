package com.zcdev.pointofsale.data.models

class Product {

    var productName:String?=null
    var productCode:Int?=null
    var productQnt:Int?=null
    var productPrice:Float?=null
    var productImg:Int?=null


    constructor(name:String,code:Int,qnt:Int,price:Float,img:Int){
        this.productName=name
        this.productCode=code
        this.productQnt=qnt
        this.productPrice=price
        this.productImg=img
    }


}