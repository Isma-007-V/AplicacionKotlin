package com.example.prealba.Cliente

import android.widget.Filter
import com.example.prealba.Administrador.ModeloCategoriaQS

class FiltrarCategoriaQS_Cliente: Filter {
    private var filtroLista: ArrayList<ModeloCategoriaQS>
    private var adaptadorCategoriaQSCliente: AdaptadorCategoria_ClienteQS

    constructor(filtroLista: ArrayList<ModeloCategoriaQS>, adaptadorCategoria: AdaptadorCategoria_ClienteQS) {
        this.filtroLista = filtroLista
        this.adaptadorCategoriaQSCliente = adaptadorCategoria
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
        adaptadorCategoriaQSCliente.categoriaArrayList = resultados.values as ArrayList<ModeloCategoriaQS>
        adaptadorCategoriaQSCliente.notifyDataSetChanged()

    }
}