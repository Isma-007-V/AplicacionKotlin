package com.example.prealba.Fragmentos_Admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.prealba.Administrador.EditarPerfilAdmin
import com.example.prealba.Administrador.MisFunciones
import com.example.prealba.Administrador.Registrar_Admin
import com.example.prealba.Elegir_rol
import com.example.prealba.R
import com.example.prealba.databinding.FragmentAdminCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Fragment_admin_cuenta : Fragment() {

    private lateinit var binding: FragmentAdminCuentaBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    //ten mucho cuidado con esto
    private lateinit var mContext : android.content.Context

    //igual aqui, esta funcion me dio algo de confusion, video 14.
    override fun onAttach(context: android.content.Context) {
        mContext = context as android.content.Context
        super.onAttach(context)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.EditarPerfilAdmin.setOnClickListener{
            startActivity(Intent(mContext, EditarPerfilAdmin::class.java))
        }

        binding.CerrarSesionAdmin.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(context, Elegir_rol::class.java))
            activity?.finishAffinity()
        }
        binding.CrearCuentaAdmin.setOnClickListener{
            startActivity(Intent(mContext, Registrar_Admin::class.java))

        }

    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener los datos del usuario actual - administrador
                    val nombres = "${snapshot.child("nombre").value}"
                    val email = "${snapshot.child("email").value}"
                    var t_registro = "${snapshot.child("tiempo_registro").value}"
                    val rol = "${snapshot.child("rol").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    if(t_registro == "null"){
                        t_registro="0"
                    }
                    //convertir a formato fecha
                    val formato_fecha = MisFunciones.formatoTiempo(t_registro.toLong())

                    //setear
                    binding.TxtNombresAdmin.text = nombres
                    binding.TxtEmailAdmin.text = email
                    binding.TxtTiempoRegistroAdmin.text = formato_fecha
                    binding.TxtRolAdmin.text= rol

                    try{
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilAdmin)
                    }catch (e:Exception){
                        Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
