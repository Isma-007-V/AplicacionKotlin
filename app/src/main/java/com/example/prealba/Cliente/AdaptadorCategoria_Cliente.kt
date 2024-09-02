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
import com.example.prealba.Modelos.ModeloCategoria
import com.example.prealba.databinding.ItemCategoriaClienteBinding


    //inicializar
    class AdaptadorCategoria_Cliente : RecyclerView.Adapter<AdaptadorCategoria_Cliente.viewHolder>, Filterable{
        private lateinit var binding: ItemCategoriaClienteBinding
        private val context : android.content.Context
        public var categoriaArrayList : ArrayList<ModeloCategoria>

        private var filtroLista : ArrayList<ModeloCategoria>
        private var filtro : FiltrarCategoria_Cliente ?= null

        constructor(context: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
            this.context = context
            this.categoriaArrayList = categoriaArrayList
            this.filtroLista = categoriaArrayList
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            binding = ItemCategoriaClienteBinding.inflate(LayoutInflater.from(context), parent, false)
            return viewHolder(binding.root)
        }

        override fun getItemCount(): Int {
            return categoriaArrayList.size
        }

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            val modelo = categoriaArrayList[position]
            val id = modelo.id
            val imagenUri = modelo.imageUrl
            val categoria = modelo.categoria
            val uid = modelo.uid
            val tiempo = modelo.tiempo

            holder.categoriaTv.text = categoria

            Glide.with(context)
                .load(imagenUri)
                .into(holder.imageCategoriaQ)

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ListaPdfCliente::class.java)
                intent.putExtra("idCategoria", id)
                intent.putExtra("tituloCategoria", categoria)
                context.startActivity(intent)
            }
        }

        inner public class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var categoriaTv : TextView = binding.ItemNombreCatC
            var imageCategoriaQ: ImageView = binding.imagenCategoriaQS

        }

        override fun getFilter(): Filter {
            if (filtro == null){
                filtro = FiltrarCategoria_Cliente(filtroLista, this)
            }
            return filtro as FiltrarCategoria_Cliente
        }

    }
