package com.example.prealba.Administrador

class ModeloPdfQS {
    var uid : String = ""
    var id : String = ""
    var titulo : String = ""
    var objetivo : String = ""
    var acciones : String = ""
    var telefono : String = ""
    var categoria : String = ""
    var url : String = ""
    var tiempo: Long = 0
    var contadorVistas: Long = 0
    var contadorDescargas : Long = 0

    constructor()
    constructor(
        uid: String,
        id: String,
        titulo: String,
        objetivo: String,
        acciones: String,
        telefono: String,
        categoria: String,
        url: String,
        tiempo: Long,
        contadorVistas: Long,
        contadorDescargas: Long
    ) {
        this.uid = uid
        this.id = id
        this.titulo = titulo
        this.objetivo = objetivo
        this.acciones = acciones
        this.telefono = telefono
        this.categoria = categoria
        this.url = url
        this.tiempo = tiempo
        this.contadorVistas = contadorVistas
        this.contadorDescargas = contadorDescargas
    }


}