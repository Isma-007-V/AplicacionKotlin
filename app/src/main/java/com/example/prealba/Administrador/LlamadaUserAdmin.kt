package com.example.prealba.Administrador

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.prealba.databinding.ActivityLlamadaUserAdminBinding

class LlamadaUserAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityLlamadaUserAdminBinding
    private var numeroTelefonico: String? = null
    private var nombreUsuario: String? = null
    private val REQUEST_PERMISSIONS_CODE = 10
    private lateinit var callReceiver: CallReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLlamadaUserAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        obtenerNumeroDeIntent()
        configurarInterfaz()
    }
    override fun onResume() {
        super.onResume()
        callReceiver = CallReceiver()
        val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(callReceiver)
    }
    class CallReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null) {
                val state = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
                if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    // La llamada ha terminado
                    (context as LlamadaUserAdmin).mostrarMensajeFinalizacion("Nombre Usuario", "Número Telefónico")
                }
            }
        }
    }


    private fun configurarInterfaz() {
        binding.IbCerrarLL.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnCancelarLlamadaA.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLLamarA.setOnClickListener {
            realizarLlamadaConPermiso()
        }
    }

    private fun obtenerNumeroDeIntent() {
        numeroTelefonico = intent.getStringExtra("numeroTelefonico")
        nombreUsuario = intent.getStringExtra("nombreUsuario")
        binding.EtNombreUser.text = nombreUsuario ?: "Nombre no disponible"
        binding.EtNumeroU.text = numeroTelefonico ?: "Número no disponible"
    }

    private fun realizarLlamadaConPermiso() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            realizarLlamada()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PERMISSIONS_CODE)
        }
    }

    private fun realizarLlamada() {
        numeroTelefonico?.let { numero ->
            val intentLlamada = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))
            startActivity(intentLlamada)
            mostrarMensajeFinalizacion(nombreUsuario ?: "Desconocido", numero)
        } ?: Log.e("LlamadaUserAdmin", "Intento de realizar una llamada con un número nulo.")
    }
    private fun mostrarMensajeFinalizacion(nombre: String, numero: String) {
        AlertDialog.Builder(this)
            .setTitle("Llamada Finalizada")
            .setMessage("Has llamado a $nombre con el número $numero.")
            .setPositiveButton("Aceptar") { dialog, which -> dialog.dismiss() }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            realizarLlamada()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                mostrarExplicacionPermisoDenegado()
            } else {
                //  mostrar una explicación breve de por qué el permiso, luego de que el usuario haya denegado la solicitud inicial.
                AlertDialog.Builder(this)
                    .setMessage("El permiso de realizar llamadas es necesario para la funcionalidad de llamada directa.")
                    .setPositiveButton("Aceptar") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PERMISSIONS_CODE)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun mostrarExplicacionPermisoDenegado() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de Llamada Denegado")
            .setMessage("Necesitas habilitar el permiso de llamadas en la configuración de la aplicación.")
            .setPositiveButton("Ir a Configuración") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
