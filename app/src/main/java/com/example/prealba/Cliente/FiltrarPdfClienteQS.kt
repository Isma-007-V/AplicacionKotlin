package com.example.prealba.Cliente

import android.widget.Filter
import com.example.prealba.Administrador.ModeloPdfQS

class FiltrarPdfClienteQS: Filter {
    var filtroList : ArrayList<ModeloPdfQS>
    var adaptadorPdfCliente : AdaptadorPdfClienteQS

    constructor(filtroList: ArrayList<ModeloPdfQS>, adaptadorPdfClienteQS: AdaptadorPdfClienteQS) {
        this.filtroList = filtroList
        this.adaptadorPdfCliente = adaptadorPdfClienteQS
    }

    override fun performFiltering(libro: CharSequence?): Filter.FilterResults {
        var libro: CharSequence? = libro
        val resultados = Filter.FilterResults()
        if(libro!=null && libro.isNotEmpty()){
            libro =libro.toString().lowercase()
            val modeloFiltrado : ArrayList<ModeloPdfQS> = ArrayList()
            for (i in filtroList.indices){
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

    override fun publishResults(p0: CharSequence?, resultados: Filter.FilterResults) {
        adaptadorPdfCliente.pdfArrayList = resultados.values as ArrayList<ModeloPdfQS>
        adaptadorPdfCliente.notifyDataSetChanged()
    }
}