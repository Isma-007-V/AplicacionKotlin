package com.example.prealba.Administrador

import android.widget.Filter

class FiltroCategoriaQS : Filter {
    private var filtroLista: ArrayList<ModeloCategoriaQS>
    private var adaptadorCategoria: AdaptadorCategoriaQS

    constructor(filtroLista: ArrayList<ModeloCategoriaQS>, adaptadorCategoria: AdaptadorCategoriaQS) {
        this.filtroLista = filtroLista
        this.adaptadorCategoria = adaptadorCategoria
    }

    override fun performFiltering(categoria: CharSequence?): FilterResults {
        var categoria = categoria
        var resultados = FilterResults()
        if(categoria !=null && categoria.isNotEmpty()){
            categoria = categoria.toString().uppercase()
            val modeloFiltrado : ArrayList<ModeloCategoriaQS> = ArrayList()
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
        adaptadorCategoria.categoriaArrayList = resultados.values as ArrayList<ModeloCategoriaQS>
        adaptadorCategoria.notifyDataSetChanged()

    }
}