package com.example.prealba.Administrador

class ModeloCategoriaQS {
    var categoria : String = ""
    var id: String=""
    var imageUrl : String? = null
    var tiempo : Long = 0
    var uid : String = ""

    constructor()
    constructor(categoria: String, id: String, imageUrl: String?, tiempo: Long, uid: String) {
        this.categoria = categoria
        this.id = id
        this.imageUrl = imageUrl
        this.tiempo = tiempo
        this.uid = uid
    }


}