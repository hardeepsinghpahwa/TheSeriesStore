package com.example.login

class cartitemdetails {
    var size: String? = null
    var sizename: String? = null
    var color: String? = null
    var colorname: String? = null
    var id: String? = null
    var image: String? = null
    var quantity: Int? = null

    constructor(size: String?, sizename: String?, color: String?, colorname: String?, id: String?, image: String?, quantity: Int?) {
        this.size = size
        this.sizename = sizename
        this.color = color
        this.colorname = colorname
        this.id = id
        this.image = image
        this.quantity = quantity
    }

    constructor() {}
}