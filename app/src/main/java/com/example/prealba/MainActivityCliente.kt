package com.example.prealba

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.Cliente.locationActivity
import com.example.prealba.Fragmento_Cliente.Fragment_Client_Favoritos
import com.example.prealba.Fragmento_Cliente.Fragment_cliente_Dashboard
import com.example.prealba.Fragmento_Cliente.Fragment_cliente_cuenta
import com.example.prealba.Fragmento_Cliente.Fragment_cliente_emergencia
import com.example.prealba.Fragmento_Cliente.Fragment_cliente_somos
import com.example.prealba.databinding.ActivityMainClienteBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivityCliente : AppCompatActivity() {

    private lateinit var binding : ActivityMainClienteBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()



        comprobarSesion()

        verFragmentoDashboardC()
        setupPopupMenu()




        //Evento de listener para elegir item y su respectiva pantalla o algo asi
        binding.BottomNavCliente.setOnItemSelectedListener {item->
            when (item.itemId){
                R.id.Menu_dashboard_cl->{
                    verFragmentoDashboardC()
                    true
                }


                R.id.Menu_fsvoritos_cl->{
                    verFragmentoFavoritosC()
                    true
                }
                R.id.Menu_Emergencias_cl->{
                    verFragmentoEmergenciaC()
                    true
                }
                R.id.Menu_somos_cl->{
                    verFragmentoSomosC()
                    true
                }

                R.id.Menu_cuenta_cl->{
                verFragmentoCuentaC()
                true
            }
                else ->{
                    false
                }
            }
        }
    }



    private fun comprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        }else{
            Toast.makeText(applicationContext, "Bienvenido(a) ${firebaseUser.email}",
                Toast.LENGTH_SHORT).show()
        }
    }

    //funciones para los fragmentos, no lo olovides

     private fun verFragmentoDashboardC(){
        val nombre_titulo = "Dashboard"
         binding.TituloRLCliente.text = nombre_titulo
         val fragment = Fragment_cliente_Dashboard()
         val fragmentTransaction = supportFragmentManager.beginTransaction()
         fragmentTransaction.replace(binding.fragmentsCliente.id,fragment, "Fragment dashboard")
         fragmentTransaction.commit()

     }

    private fun verFragmentoFavoritosC(){
        val nombre_titulo = "Favoritos"
        binding.TituloRLCliente.text = nombre_titulo
        val fragment = Fragment_Client_Favoritos()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id,fragment, "Fragment Favoritos")
        fragmentTransaction.commit()
    }

    private fun verFragmentoSomosC(){
        val nombre_titulo = "Prealba: Instituciones auxiliares"
        binding.TituloRLCliente.text = nombre_titulo
        val fragment = Fragment_cliente_somos()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id,fragment, "Fragment Favoritos")
        fragmentTransaction.commit()
    }
    private fun verFragmentoCuentaC(){
        val nombre_titulo = "Cuenta"
        binding.TituloRLCliente.text = nombre_titulo
        val fragment = Fragment_cliente_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id,fragment, "Fragment Cuenta")
        fragmentTransaction.commit()
    }
   private fun verFragmentoEmergenciaC(){
       val nombre_titulo = "Emergencia"
       binding.TituloRLCliente.text = nombre_titulo
       val fragment = Fragment_cliente_emergencia()
       val fragmentTransaction = supportFragmentManager.beginTransaction()
       fragmentTransaction.replace(binding.fragmentsCliente.id,fragment, "Fragment  Emergencia")
       fragmentTransaction.commit()
    }
     private fun setupPopupMenu() {
        binding.IbMenu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.inflate(R.menu.menu2)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_option1 -> {
                        // Iniciar una actividad
                        val intent = Intent(this, locationActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    // Añade más casos si tienes más ítems
                    else -> false
                }
            }
            popup.show()
        }
    }

}

