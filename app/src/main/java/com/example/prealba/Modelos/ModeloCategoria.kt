package com.example.prealba.Modelos

class ModeloCategoria {
    var id : String = ""
    var categoria : String = ""
    var imageUrl : String? = null
    var tiempo : Long = 0
    var uid : String = ""

    constructor()
    constructor(id: String, categoria: String, imageUrl: String?, tiempo: Long, uid: String) {
        this.id = id
        this.categoria = categoria
        this.imageUrl = imageUrl
        this.tiempo = tiempo
        this.uid= uid
    }

}