package com.example.prealba.Administrador

import android.app.AlertDialog
import android.content.Context
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
import com.example.prealba.databinding.ItemCategoriaqsAdminBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorCategoriaQS : RecyclerView.Adapter<AdaptadorCategoriaQS.HolderCategoriaQS>, Filterable{
    private lateinit var binding : ItemCategoriaqsAdminBinding
    private val m_context : android.content.Context
    public var categoriaArrayList : ArrayList<ModeloCategoriaQS>

    private var filtroListaQS : ArrayList<ModeloCategoriaQS>
    private var filtro : FiltroCategoriaQS? = null

    constructor(m_context: Context, categoriaArrayList:ArrayList<ModeloCategoriaQS>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
        this.filtroListaQS = categoriaArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaQS {
        binding = ItemCategoriaqsAdminBinding.inflate(LayoutInflater.from(m_context), parent, false )
        return HolderCategoriaQS(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaQS, position: Int) {
        val modelo = categoriaArrayList[position]
        val imagenUri = modelo.imageUrl
        val id = modelo.id
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTvQS.text = categoria

        Glide.with(m_context)
            .load(imagenUri)
            .into(holder.imageCategoriaQS)


        holder.eliminarCatIbQS.setOnClickListener{
            var builder = AlertDialog.Builder(m_context)
            builder.setTitle("Eliminar categoria")
                .setMessage("¿Seguro que desea eliminar esta categoria?")
                .setPositiveButton("Confirmar"){a,d->
                    Toast.makeText(m_context, "Eliminando categoria", Toast.LENGTH_SHORT).show()

                    val imageUrl = modelo.imageUrl

                    EliminarCategoriaQS(modelo, holder)

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
        // Aqui Configura un clic en un elemento de la vista representada por holder. Cuando el elemento se hace clic, se ejecutará
        // el código dentro del bloque.
        holder.itemView.setOnClickListener{

            // posterior a ello crea una nueva intención (Intent) que se utiliza para iniciar una nueva actividad (ListaPdfAdminQS). Se especifica el contexto
            // (m_context) y la clase de destino (ListaPdfAdminQS::class.java) para la actividad que se iniciará.
            val intent = Intent(m_context, ListaPdfAdminQS::class.java)
            intent.putExtra("idCategoria", id)
            intent.putExtra("tituloCategoria", categoria)
            m_context.startActivity(intent)
        }

    }
    private fun EliminarCategoriaQS(modelo: ModeloCategoriaQS, holder: AdaptadorCategoriaQS.HolderCategoriaQS) {
        val id= modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("CategoriaQS")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "Categoria eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(m_context, "No se pudo eliminar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class  HolderCategoriaQS (itemView: View) : RecyclerView.ViewHolder(itemView){
        var categoriaTvQS : TextView = binding.ItemNombreCatAQS
        var imageCategoriaQS: ImageView = binding.imagenCategoriaQS
        var eliminarCatIbQS : ImageButton = binding.EliminarCatQS
    }

    override fun getFilter(): Filter {
        if(filtro == null) {
            filtro = FiltroCategoriaQS(filtroListaQS, this)
        }
        return filtro as FiltroCategoriaQS
    }


}