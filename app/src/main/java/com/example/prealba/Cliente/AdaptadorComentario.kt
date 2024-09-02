package com.example.prealba.Cliente

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prealba.Administrador.MisFunciones
import com.example.prealba.Modelos.ModeloComentario
import com.example.prealba.R
import com.example.prealba.databinding.ItemComentarioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorComentario : RecyclerView.Adapter<AdaptadorComentario.HolcerComentario>{

    val context : android.content.Context
    val comentarioArrayList : ArrayList<ModeloComentario>

    private lateinit var binding : ItemComentarioBinding

    constructor(context: Context, comentarioArrayList: ArrayList<ModeloComentario>) {
        this.context = context
        this.comentarioArrayList = comentarioArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolcerComentario {
        binding = ItemComentarioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolcerComentario(binding.root)
    }

    override fun getItemCount(): Int {

        return comentarioArrayList.size
    }

    override fun onBindViewHolder(holder: HolcerComentario, position: Int) {
        val modelo = comentarioArrayList[position]

        val id = modelo.id
        val idLibros = modelo.idLibro
        val comentario = modelo.comentario
        val uid = modelo.comentario
        val tiempo = modelo.tiempo
        
        val fecha = MisFunciones.formatoTiempo(tiempo.toLong())

        holder.Tv_fecha.text = fecha
        holder.Tv_comentario.text = comentario 
        
        cargarInformacion(modelo, holder)

         holder.itemView.setOnClickListener{
             dialogEliminarCom(modelo, holder)
         }
    }

    private fun dialogEliminarCom(modelo: ModeloComentario, holder: AdaptadorComentario.HolcerComentario) {
       val builder = AlertDialog.Builder(context)
       builder.setTitle("Eliminar comentario")
           .setMessage("¿Estas seguro(a) de eliminar el comentario?")
           .setPositiveButton("Eliminar"){d,e->
               val idLibro = modelo.idLibro
               val idCom = modelo.id

               val ref = FirebaseDatabase.getInstance().getReference("Libros")
               ref.child(idLibro).child("Comentarios").child(idCom)
                   .removeValue()
                   .addOnSuccessListener {
                       Toast.makeText(context, "Comentario eliminado", Toast.LENGTH_SHORT).show()

                   }
                   .addOnFailureListener { e->
                       Toast.makeText(context, "Falló la eliminación del comentario debido a ${e.message}",
                           Toast.LENGTH_SHORT).show()

                   }


           }
           .setNegativeButton("Cancelar"){d,e->
                d.dismiss()
           }
           .show()
    }

    private fun cargarInformacion(modelo: ModeloComentario, holder: AdaptadorComentario.HolcerComentario) {
        val uid = modelo.uid

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombre").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    holder.Iv_nombres.text = nombre

                    try{
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(holder.Iv_imagen)
                    }catch (e:Exception){
                        holder.Iv_imagen.setImageResource(R.drawable.ic_img_perfil)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    inner class HolcerComentario (itemView: View) : RecyclerView.ViewHolder(itemView){
        val Iv_imagen = binding.IvImgPerfil
        val Iv_nombres = binding.TvNombresC
        val Tv_fecha = binding.TvFechaC
        val Tv_comentario = binding.TvComentariosC
    }

}