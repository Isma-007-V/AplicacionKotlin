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
import com.example.prealba.databinding.ItemEmergenciasAdminBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorCategoriaLlamadasAdmin: RecyclerView.Adapter<AdaptadorCategoriaLlamadasAdmin.HolderCategoriaLlamadasOrgAd>, Filterable {
    private lateinit var binding : ItemEmergenciasAdminBinding
    private val m_context : android.content.Context
    public var categoriaArrayList : ArrayList<ModeloCategoriaLlamadasAdmin>

    private var filtroListaLL : ArrayList<ModeloCategoriaLlamadasAdmin>
    private var filtroLL : FiltroCastegoriaLlamada? = null

    constructor(m_context: Context, categoriaArrayList: ArrayList<ModeloCategoriaLlamadasAdmin>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
        this.filtroListaLL = categoriaArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaLlamadasOrgAd {
        binding = ItemEmergenciasAdminBinding.inflate(LayoutInflater.from(m_context), parent, false )
        return HolderCategoriaLlamadasOrgAd(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaLlamadasOrgAd, position: Int) {
        val modelo = categoriaArrayList[position]
        val imagenUri = modelo.imageUrl
        val id = modelo.id
        val categoria = modelo.categoria
        val telefonoOrgLla = modelo.numeroTelefonico
        val tiempo = modelo.tiempo
        val uid = modelo.uid


        holder.numeroTvLlamadaAdmin.text = telefonoOrgLla

        holder.categoriaTvOrg.text = categoria

        Glide.with(m_context)
            .load(imagenUri)
            .into(holder.imageCategoriaQSOrg)
        holder.opcionesActCatQS2.setOnClickListener {
            verOpciones(modelo, holder)
        }
        

       holder.eliminarCatIbQS.setOnClickListener{
           var builder = AlertDialog.Builder(m_context)
           builder.setTitle("Eliminar categoria")
               .setMessage("¿Seguro que desea eliminar esta categoria?")
               .setPositiveButton("Confirmar"){a,d->
                   Toast.makeText(m_context, "Eliminando categoria", Toast.LENGTH_SHORT).show()

                   val imageUrl = modelo.imageUrl

                   EliminarCategoriaLlamadasA(modelo, holder)

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
            Toast.makeText(m_context, "Correcto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verOpciones(modelo: ModeloCategoriaLlamadasAdmin, holder: AdaptadorCategoriaLlamadasAdmin.HolderCategoriaLlamadasOrgAd) {
        val idcategoria = modelo.id
        val imagenUrlC = modelo.imageUrl
        val categoriaTitulo = modelo.categoria
        val numeroCategoria = modelo.numeroTelefonico

        val opciones = arrayOf("Actualizar")

        var builder = AlertDialog.Builder(m_context)
        builder.setTitle("Seleccione una opción")
            .setItems(opciones){ dialog, posicion->
                if(posicion == 0){
                    //actualizar
                    val intent = Intent(m_context, ActualizarCatLL::class.java)
                    intent.putExtra("idCatLL", idcategoria)
                    m_context.startActivity(intent)
                }else if(posicion == 1){
                    //eliminar

                }
            }
            .show()
     }


    private fun EliminarCategoriaLlamadasA(modelo: ModeloCategoriaLlamadasAdmin, holder: AdaptadorCategoriaLlamadasAdmin.HolderCategoriaLlamadasOrgAd) {
        val id= modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("OrganismoNumeros")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "Categoria eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(m_context, "No se pudo eliminar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class  HolderCategoriaLlamadasOrgAd (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoriaTvOrg : TextView = binding.ItemNombreCatAQS
        var numeroTvLlamadaAdmin : TextView = binding.EtTelefonoOrLibroAdminLLa
        var imageCategoriaQSOrg: ImageView = binding.imagenCategoriaQS
        var eliminarCatIbQS : ImageButton = binding.EliminarCatQS
        var opcionesActCatQS2: ImageButton = binding.IbMasOpciones2
    }

    override fun getFilter(): Filter {
        if(filtroLL == null) {
            filtroLL = FiltroCastegoriaLlamada(filtroListaLL, this)
        }
        return filtroLL as FiltroCastegoriaLlamada
    }
    }


