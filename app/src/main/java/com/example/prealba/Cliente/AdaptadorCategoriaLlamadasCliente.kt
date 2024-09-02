package com.example.prealba.Cliente

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prealba.Administrador.ModeloCategoriaLlamadasAdmin
import com.example.prealba.databinding.ItemEmergenciasClienteBinding
import com.google.firebase.auth.FirebaseAuth

class AdaptadorCategoriaLlamadasCliente(
    private val m_context: Context,
    private val categoriaArrayList: ArrayList<ModeloCategoriaLlamadasAdmin>
) : RecyclerView.Adapter<AdaptadorCategoriaLlamadasCliente.HolderCategoriaLlamadasOrgAd>() {

    private lateinit var binding: ItemEmergenciasClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaLlamadasOrgAd {
        binding = ItemEmergenciasClienteBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderCategoriaLlamadasOrgAd(binding.root)
    }
    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }
    @RequiresApi(Build.VERSION_CODES.N)
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

        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, LLamada::class.java)
            intent.putExtra("categoria", categoria)
            intent.putExtra("numeroTelefonico", telefonoOrgLla)
            m_context.startActivity(intent)
        }
    }
    inner class HolderCategoriaLlamadasOrgAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoriaTvOrg: TextView = binding.ItemNombreCatAQS
        var numeroTvLlamadaAdmin: TextView = binding.EtTelefonoOrLibroQS
        var imageCategoriaQSOrg: ImageView = binding.imagenCategoriaQS
    }
}