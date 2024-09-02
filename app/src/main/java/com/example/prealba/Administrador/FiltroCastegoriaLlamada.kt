package com.example.prealba.Administrador

import android.widget.Filter

class FiltroCastegoriaLlamada: Filter{

    private var filtroLista: ArrayList<ModeloCategoriaLlamadasAdmin>
    private var adaptadorCategoria: AdaptadorCategoriaLlamadasAdmin

    constructor(filtroLista: ArrayList<ModeloCategoriaLlamadasAdmin>, adaptadorCategoria: AdaptadorCategoriaLlamadasAdmin) {
        this.filtroLista = filtroLista
        this.adaptadorCategoria = adaptadorCategoria
    }

    override fun performFiltering(categoria: CharSequence?): FilterResults {
        var categoria = categoria
        var resultados = FilterResults()
        if(categoria !=null && categoria.isNotEmpty()){
            categoria = categoria.toString().uppercase()
            val modeloFiltrado : ArrayList<ModeloCategoriaLlamadasAdmin> = ArrayList()
            for (i in 0 until filtroLista.size){
                if(filtroLista[i].categoria.uppercase().contains(categoria)){
                    modeloFiltrado.add(filtroLista[i])
                }
                resultados.count = modeloFiltrado.size
                resultados.values = modeloFiltrado

            }
        }
        else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }


    override fun publishResults(constraint: CharSequence?, resultados: FilterResults) {
        adaptadorCategoria.categoriaArrayList = resultados.values as ArrayList<ModeloCategoriaLlamadasAdmin>
        adaptadorCategoria.notifyDataSetChanged()
    }
}