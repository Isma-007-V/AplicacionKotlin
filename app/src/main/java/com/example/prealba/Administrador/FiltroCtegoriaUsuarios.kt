package com.example.prealba.Administrador

import android.widget.Filter
import com.example.prealba.Modelos.ModeloDatosUsuario

class FiltroCtegoriaUsuarios:Filter {
    private var filtroListaU: ArrayList<ModeloDatosUsuario>
    private var adaptadorListaUsuarios: AdaptadorListaUsuarios

    constructor(filtroListaUsuarios: ArrayList<ModeloDatosUsuario>, adaptadorListaUsuarios: AdaptadorListaUsuarios) {
        this.filtroListaU = filtroListaUsuarios
        this.adaptadorListaUsuarios = adaptadorListaUsuarios
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val resultados = FilterResults()

        // Verifica si el término de búsqueda no es nulo y no está vacío
        if (!constraint.isNullOrBlank()) {
            val filtroTexto = constraint.toString().uppercase()
            val modeloFiltradoU: ArrayList<ModeloDatosUsuario> = ArrayList()

            // Se filtra la lista original basada en el nombre
            filtroListaU.filterTo(modeloFiltradoU) {
                it.nombre.uppercase().contains(filtroTexto)
            }

            resultados.values = modeloFiltradoU
            resultados.count = modeloFiltradoU.size
        } else {
            // Si el texto es nulo o vacío, devolvera  la lista original
            resultados.values = filtroListaU
            resultados.count = filtroListaU.size
        }

        return resultados
    }

    override fun publishResults(constraint: CharSequence?, resultados: FilterResults?) {
        adaptadorListaUsuarios.usuariosArrayList = resultados?.values as ArrayList<ModeloDatosUsuario>
        adaptadorListaUsuarios.notifyDataSetChanged()
    }


}