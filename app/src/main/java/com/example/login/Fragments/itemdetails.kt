package com.example.login.Fragments

class itemdetails {
    var image: String? = null
    var name: String? = null
    var rating: String? = null
    var category: String? = null
    var subcategory: String? = null
    var price: String? = null
    var product: String? = null
    var detail1: String? = null
    var detail2: String? = null
    var detail3: String? = null
    var detail4: String? = null
    var id: String? = null

    constructor(image: String?, name: String?, rating: String?, category: String?, price: String?, id: String?, product: String?) {
        this.image = image
        this.name = name
        this.rating = rating
        this.category = category
        this.price = price
        this.id = id
        this.product = product
    }

    constructor(image: String?, name: String?, rating: String?, category: String?, subcategory: String?, price: String?, product: String?, detail1: String?, detail2: String?, detail3: String?, detail4: String?, id: String?) {
        this.image = image
        this.name = name
        this.rating = rating
        this.category = category
        this.subcategory = subcategory
        this.price = price
        this.product = product
        this.detail1 = detail1
        this.detail2 = detail2
        this.detail3 = detail3
        this.detail4 = detail4
        this.id = id
    }

    constructor() {}
}