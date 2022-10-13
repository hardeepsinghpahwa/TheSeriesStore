package com.example.login.dataclass

class addressdetail {
    var name: String? = null
    var phone: String? = null
    var address: String? = null
    var city: String? = null
    var state: String? = null
    var pincode: String? = null
    var type: String? = null

    constructor() {}
    constructor(name: String?, phone: String?, address: String?, city: String?, state: String?, pincode: String?, type: String?) {
        this.name = name
        this.phone = phone
        this.address = address
        this.city = city
        this.state = state
        this.pincode = pincode
        this.type = type
    }
}