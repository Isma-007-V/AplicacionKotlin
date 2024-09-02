package com.example.prealba.Administrador

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prealba.Modelos.ModeloCategoria
import com.example.prealba.databinding.ItemCategoriaAdminBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorCategoria: RecyclerView.Adapter<AdaptadorCategoria.HolderCategoria>, Filterable{
    private lateinit var  binding : ItemCategoriaAdminBinding
    private val m_context : android.content.Context
    public var categoriaArrayList : ArrayList<ModeloCategoria>

    private var filtroLista : ArrayList<ModeloCategoria>
    private var filtro : FiltroCategoria? = null

    constructor(m_context: android.content.Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
        this.filtroLista=categoriaArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaAdminBinding.inflate(LayoutInflater.from(m_context), parent, false )
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
      return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modelo = categoriaArrayList[position]
        val id = modelo.id
        val imagenUri = modelo.imageUrl
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria

        Glide.with(m_context)
            .load(imagenUri)
            .into(holder.imageCategoriaQ)

        holder.eliminarCatIb.setOnClickListener{
            var builder = AlertDialog.Builder(m_context)
            builder.setTitle("Eliminar categoria")
                .setMessage("¿Seguro que desea eliminar esta categoria?")
                .setPositiveButton("Confirmar"){a,d->
                    Toast.makeText(m_context, "Eliminando categoria", Toast.LENGTH_SHORT).show()

                    val imageUrl = modelo.imageUrl

                    EliminarCategoria(modelo, holder)

                    if (!imageUrl.isNullOrBlank()) {
                        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

                        // 4. Elimina el archivo de almacenamiento
                        storageRef.delete().addOnSuccessListener {
                            Toast.makeText(m_context, "Imagen eliminada del almacenamiento", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(m_context, "No se pudo eliminar la imagen del almacenamiento: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                .setNegativeButton("Cancelar"){a,d->
                    a.dismiss()
                }
            builder.show()
        }


        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, ListaPdfAdmin::class.java)
            intent.putExtra("idCategoria", id)
            intent.putExtra("tituloCategoria", categoria)
            m_context.startActivity(intent)
        }

    }

    private fun EliminarCategoria(modelo: ModeloCategoria, holder: AdaptadorCategoria.HolderCategoria) {
        val id= modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categoria")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "Categoria eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(m_context, "No se pudo eliminar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    inner class  HolderCategoria (itemView: View) : RecyclerView.ViewHolder(itemView){
        var categoriaTv : TextView = binding.ItemNombreCatA
        var imageCategoriaQ: ImageView = binding.imagenCategoriaQS
        var eliminarCatIb : ImageButton = binding.EliminarCat
    }

    override fun getFilter(): Filter {
        if(filtro == null){
            filtro = FiltroCategoria(filtroLista, this)
        }
        return filtro as FiltroCategoria
    }

}