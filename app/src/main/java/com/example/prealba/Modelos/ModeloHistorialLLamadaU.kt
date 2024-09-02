package com.example.prealba.Modelos

class ModeloHistorialLLamadaU {
     var duracion: String = ""
     var fecha: String = ""
     var latitud : String= ""
     var longitud:String = ""
     var tipo: String = ""
    var uid: String = ""
    var nombre: String = ""
    var telefono: String = ""


    constructor()
    constructor(duracion: String, fecha: String, latitud: String, longitud: String, tipo: String,uid: String, nombre: String, telefono: String) {
        this.duracion = duracion
        this.fecha = fecha
        this.latitud = latitud
        this.longitud = longitud
        this.tipo = tipo
        this.uid = uid
        this.nombre = nombre
        this.telefono = telefono
    }

}