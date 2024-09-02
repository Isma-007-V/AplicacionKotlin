package com.example.prealba.Modelos

class ModeloDatosUsuario {
        var uid: String = ""
        var email: String = ""
        var localidad: String = ""
        var nombre: String = ""
        var nss: String = ""
        var rol: String = ""
        var telefono: String = ""

        // Constructor por defecto necesario para Firebase
        constructor()

        // Constructor con parámetros
        constructor(uid: String, email: String, localidad: String, nombre: String, nss: String, rol: String, telefono: String) {
            this.uid = uid
            this.email = email
            this.localidad = localidad
            this.nombre = nombre
            this.nss = nss
            // Solo establece el rol si es "cliente" o "estándar"
            if (rol == "cliente") {
                this.rol = rol
            }
            this.telefono = telefono

        }
}