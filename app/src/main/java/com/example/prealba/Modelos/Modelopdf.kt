package com.example.prealba.Modelos

class Modelopdf {
    var uid : String = ""
    var id : String = ""
    var titulo : String = ""
    var descripcion : String = ""
    var categoria : String = ""
    var url : String = ""
    var tiempo : Long = 0
    var contadorVistas : Long = 0
    var contadorDescargas : Long = 0
    var esFavorito = false
    //requerido por firebase para hacer lectura de informacion
    constructor()
    constructor(
        uid: String,
        id : String,
        titulo : String,
        descripcion : String,
        categoria : String,
        url : String,
        tiempo : Long,
        contadorVistas : Long,
        contadorDescargas : Long,
        esFavorito : Boolean
    ){

        this.uid = uid
        this.id = id
        this.titulo = titulo
        this.descripcion = descripcion
        this.categoria = categoria
        this.url = url
        this.tiempo = tiempo
        this.contadorVistas = contadorVistas
        this.contadorDescargas = contadorDescargas
        this.esFavorito = esFavorito
    }


}