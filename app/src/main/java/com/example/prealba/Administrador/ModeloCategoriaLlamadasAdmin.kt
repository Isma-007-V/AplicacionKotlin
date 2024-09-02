package com.example.prealba.Administrador

class ModeloCategoriaLlamadasAdmin {
    var categoria : String = ""
    var numeroTelefonico : String = ""
    var id: String=""
    var imageUrl : String? = null
    var tiempo : Long = 0
    var uid : String = ""

    constructor()
    constructor(categoria: String, numeroTelefonico: String, id: String, imageUrl: String?, tiempo: Long, uid: String) {
        this.categoria = categoria
        this.numeroTelefonico = numeroTelefonico
        this.id = id
        this.imageUrl = imageUrl
        this.tiempo = tiempo
        this.uid = uid
    }

}
