package com.example.prealba.Cliente

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prealba.Administrador.ModeloCategoriaQS
import com.example.prealba.databinding.ItemCategoriaCQsBinding

class AdaptadorCategoria_ClienteQS: RecyclerView.Adapter<AdaptadorCategoria_ClienteQS.HolderCategoriaQSCL>, Filterable{

    private lateinit var binding : ItemCategoriaCQsBinding
    private val m_context : android.content.Context
    public var categoriaArrayList : ArrayList<ModeloCategoriaQS>

    private var filtroListaQSCL : ArrayList<ModeloCategoriaQS>
    private var filtroCL : FiltrarCategoriaQS_Cliente? = null

    constructor(m_context: Context, categoriaArrayList: ArrayList<ModeloCategoriaQS>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
        this.filtroListaQSCL = categoriaArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaQSCL {
        binding = ItemCategoriaCQsBinding.inflate(LayoutInflater.from(m_context), parent, false )
        return HolderCategoriaQSCL(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaQSCL, position: Int) {
        val modelo = categoriaArrayList[position]
        val imagenUri = modelo.imageUrl
        val id = modelo.id
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTvQSCL.text = categoria

        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, ListaPdfClienteQs::class.java)
            intent.putExtra("idCategoria", id)
            intent.putExtra("tituloCategoria", categoria)
            m_context.startActivity(intent)
        }
        Glide.with(m_context)
            .load(imagenUri)
            .into(holder.imageCategoriaQSCL)

    }

    inner class  HolderCategoriaQSCL (itemView: View) : RecyclerView.ViewHolder(itemView){
        var categoriaTvQSCL : TextView = binding.ItemNombreCatAQS
        var imageCategoriaQSCL: ImageView = binding.imagenCategoriaQSCL
        //var eliminarCatIbQSCL : ImageButton = binding.EliminarCatQSCL
    }

    override fun getFilter(): Filter {
        if(filtroCL == null){
            filtroCL = FiltrarCategoriaQS_Cliente(filtroListaQSCL, this)
        }
        return filtroCL as FiltrarCategoriaQS_Cliente
    }


}