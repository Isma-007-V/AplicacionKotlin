package com.example.prealba.Administrador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prealba.Modelos.ModeloDatosUsuario
import com.example.prealba.databinding.ItemUsuarioEstandarBinding

class AdaptadorListaUsuarios : RecyclerView.Adapter<AdaptadorListaUsuarios.HolderListaUsuarios>, Filterable{
    private lateinit var binding : ItemUsuarioEstandarBinding

    private val m_context : android.content.Context
    public var usuariosArrayList : ArrayList<ModeloDatosUsuario>

    private var filtroListaUsers : ArrayList<ModeloDatosUsuario>
    private var filtroU : FiltroCtegoriaUsuarios? = null

    constructor(m_context: Context, usuariosArrayList: ArrayList<ModeloDatosUsuario>) {
        this.m_context = m_context
        this.usuariosArrayList = usuariosArrayList
        this.filtroListaUsers = usuariosArrayList
    }

    inner class HolderListaUsuarios(itemView:View) :RecyclerView.ViewHolder(itemView){
        var nombreuser : TextView = binding.TxtNombresClientes2
        val correoU : TextView = binding.TxtEmailCliente2
        val numerTelefonico : TextView = binding.TxtTelefonoCliente2
        val numeroSS : TextView = binding.TxtNSSCl2
        val localidadU : TextView = binding.TxtLocalidadCl2
        val rolUsuario : TextView = binding.TxtRolCliente2
        val llamarUser : ImageButton = binding.btnRealizarLLamadas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderListaUsuarios {
        binding = ItemUsuarioEstandarBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderListaUsuarios(binding.root)
    }

    override fun getItemCount(): Int {
        return usuariosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderListaUsuarios, position: Int) {
        val modelo = usuariosArrayList[position]
        val nombreu = modelo.nombre
        val mail=modelo.email
        val cel = modelo.telefono
        val vive = modelo.localidad
        val numeroseguro = modelo.nss
        val rolqueuso = modelo.rol
        val uidusuario = modelo.uid

        holder.nombreuser.text=nombreu
        holder.correoU.text = mail
        holder.numerTelefonico.text = cel
        holder.localidadU.text= vive
        holder.numeroSS.text= numeroseguro
        holder.rolUsuario.text = rolqueuso

        holder.llamarUser.setOnClickListener {
            val intent = Intent(m_context, LlamadaUserAdmin::class.java)
            intent.putExtra("nombreUsuario", nombreu)
            intent.putExtra("numeroTelefonico", cel)
            m_context.startActivity(intent)
        }

    }

    override fun getFilter(): Filter {
        if (filtroU == null){
            filtroU = FiltroCtegoriaUsuarios(filtroListaUsers, this)

        }
        return filtroU as FiltroCtegoriaUsuarios
    }
}