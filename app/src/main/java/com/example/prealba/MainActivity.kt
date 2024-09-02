package com.example.prealba

import Fragment_panel_informe
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.Fragmentos_Admin.Fragment_admin_cuenta
import com.example.prealba.Fragmentos_Admin.Fragment_admin_dashboard
import com.example.prealba.Fragmentos_Admin.Fragment_admin_emergencias
import com.example.prealba.Fragmentos_Admin.Fragment_admin_somos
import com.example.prealba.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    //realizar conexion de componentes en el mainActivity y vista

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()
        VerFragmentoDashboard()
        //appcheck
        /* Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
        PlayIntegrityAppCheckProviderFactory.getInstance(),
        )*/

        binding.IbMenu.setOnClickListener {
            showPopupMenu(it)
        }
        

        // identificador de diseño
        binding.BottomNvAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                //seccion panel de inicio
                R.id.Menu_panel -> {
                    VerFragmentoDashboard()
                    true
                }
                //seccion panel de cuenta para configuracion de la misma
                R.id.Menu_cuenta -> {
                    VerFragmentoCuenta()
                    true
                }
                //quienes somos
                R.id.Menu_somos -> {
                    VerFragmentoSomos()
                    true
                }
                //emergencias
                R.id.Menu_emergencias_A -> {
                    VerFragmentoEmergencias()
                    true
                }
                //menu administrador
                R.id.Menu_Panel_Menu_A -> {
                    VerFragmentoPanelInformes()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun VerFragmentoDashboard() {
        val nombre_titulo = "Inicio"
        binding.TituloRLAdmin.text = nombre_titulo

        //visualizar fragmento dentro del framelayout
        val fragment = Fragment_admin_dashboard()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment Inicio")
        fragmentTransaction.commit()

    }

    private fun VerFragmentoEmergencias() {
        val nombre_titulo = "Emergencias"
        binding.TituloRLAdmin.text = nombre_titulo

        //visualizar fragmento dentro del framelayout
        val fragment = Fragment_admin_emergencias()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment Emergencias")
        fragmentTransaction.commit()

    }

    private fun VerFragmentoSomos() {
        val nombre_titulo = "Quienes somos"
        binding.TituloRLAdmin.text = nombre_titulo

        //visualizar fragmento dentro del framelayout
        val fragment = Fragment_admin_somos()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment quienes somos")
        fragmentTransaction.commit()

    }

    private fun VerFragmentoPanelInformes() {
        val nombre_titulo = "Informes"
        binding.TituloRLAdmin.text = nombre_titulo

        //visualizar fragmento dentro del framelayout
        val fragment = Fragment_panel_informe()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment Informes")
        fragmentTransaction.commit()

    }

    private fun VerFragmentoCuenta() {
        val nombre_titulo = "Mi cuenta"
        binding.TituloRLAdmin.text = nombre_titulo

        //visualizar fragmento dentro del framelayout
        val fragment = Fragment_admin_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment mi cuenta")
        fragmentTransaction.commit()

    }

    private fun ComprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        } else {
            //Toast.makeText(applicationContext, "Bienvenido(a) ${firebaseUser.email}",
            //Toast.LENGTH_SHORT).show()
        }

    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu2a, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.m_hll -> {
                    openLink("https://docs.google.com/spreadsheets/d/1eBN1SyisssKIpzCEmo0qxe96RXl0FYoPxH0-c42TVZo/edit#gid=0")
                    true
                }

                R.id.m_us-> {
                    openLink("https://docs.google.com/spreadsheets/d/1QYDpapuWlubta4rgw-SOoLP7K4uvhJarONB3TmGG-es/edit#gid=0")
                    true
                }

                R.id.m_ad -> {
                    openLink("https://docs.google.com/spreadsheets/d/12uPkA9CNuHD8PEZh5Z4uGFrBCh4OgYEtMyaihARWnhk/edit#gid=0")
                    true
                }
                R.id.m_oa -> {
                    openLink("https://docs.google.com/spreadsheets/d/1Yz79mx8HOUiW0ycKPjw2CWEBPzSbAIctcyjcswsLd6I/edit#gid=0")
                    true
                }

                R.id.m_ull-> {
                    openLink("https://docs.google.com/spreadsheets/d/1-ODagCJ8vk0m3-a0kwQTrEWcs7-LSsvZRQIU6ekyiYc/edit#gid=1716159675")
                    true
                }

                R.id.m_arc -> {
                    openLink("https://docs.google.com/spreadsheets/d/1QFDtfl-RWsUoh_2Usa8-AeEGx19pQqutZ3WMdur0NIw/edit#gid=0")
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}





