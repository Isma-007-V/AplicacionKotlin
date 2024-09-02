package com.example.prealba

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.Administrador.Login_admin
import com.example.prealba.Cliente.Registrar_Cliente
import com.example.prealba.databinding.ActivityElegirRolBinding

class Elegir_rol : AppCompatActivity() {
    private lateinit var binding : ActivityElegirRolBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElegirRolBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //appcheck
        /*Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
        PlayIntegrityAppCheckProviderFactory.getInstance(),
        )*/

        binding.BtnRolAdministrador.setOnClickListener{
            //Toast.makeText(applicationContext, "Rol administrador", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Elegir_rol, Login_admin::class.java))
        }
       /* binding.BtnRolCliente.setOnClickListener{
            Toast.makeText(applicationContext, "Rol Estandar", Toast.LENGTH_SHORT).show()
        }*/
        binding.BtnRolCliente.setOnClickListener{
            startActivity(Intent(this@Elegir_rol, Registrar_Cliente::class.java))
        }
    }
}