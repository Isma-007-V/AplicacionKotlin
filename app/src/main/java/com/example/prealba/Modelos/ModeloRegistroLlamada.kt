package com.example.prealba.Modelos

class ModeloRegistroLlamada {
    var numeroTelefono: String=""
    var tipoLlamada: Int = 0
    var duracionLlamada: String=""
    var fechaLlamada: Long = 0

    constructor()
    constructor(
        numeroTelefono: String,
        tipoLlamada: Int,
        duracionLlamada: String,
        fechaLlamada: Long
    ) {
        this.numeroTelefono = numeroTelefono
        this.tipoLlamada = tipoLlamada
        this.duracionLlamada = duracionLlamada
        this.fechaLlamada = fechaLlamada
    }

}