package com.example.prealba.Administrador

import android.widget.Filter

class FiltroPdfAdminQS : Filter{

    var filtroList : ArrayList<ModeloPdfQS>
    var adaptadorPdfAdmin : AdaptadorPdfAdminQS

    constructor(filtroList: ArrayList<ModeloPdfQS>, adaptadorPdfAdmin: AdaptadorPdfAdminQS) : super() {
        this.filtroList = filtroList
        this.adaptadorPdfAdmin = adaptadorPdfAdmin
    }

    override fun performFiltering(libro: CharSequence?): FilterResults {
        var libro: CharSequence? = libro
        val resultados = FilterResults()
        if(libro!=null && libro.isNotEmpty()){
            libro =libro.toString().lowercase()
            val modeloFiltrado : ArrayList<ModeloPdfQS> = ArrayList()
            for (i in filtroList.indices){
                //si coincide con lo que se escribe
                if(filtroList[i].titulo.lowercase().contains(libro)){
                    modeloFiltrado.add(filtroList[i])
                }
            }
            resultados.count = modeloFiltrado.size
            resultados.values = modeloFiltrado
        }
        else{
            resultados.count = filtroList.size
            resultados.values = filtroList
        }
        return resultados
    }

    override fun publishResults(constraint: CharSequence?, resultados: FilterResults) {
        adaptadorPdfAdmin.pdfArrayList = resultados.values as ArrayList<ModeloPdfQS>
        adaptadorPdfAdmin.notifyDataSetChanged()
    }
}