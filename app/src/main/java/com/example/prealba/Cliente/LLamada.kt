package com.example.prealba.Cliente

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.prealba.databinding.ActivityLlamadaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.Locale

class LLamada : AppCompatActivity() {
    private lateinit var binding: ActivityLlamadaBinding
    private var numeroTelefonico: String? = null
    //private val PERMISSION_REQUEST_CALL_PHONE = 1
    private val REQUEST_PERMISSIONS_CODE = 10
    //private val REQUEST_READ_CALL_LOG_PERMISSION = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private var llamadaIniciadaPorApp: Boolean = false
    private lateinit var firebaseAuth: FirebaseAuth
    private var callReceiver: CallReceiver? = null
    private var ubicacionMostrada = false
    //private  var infroamcionguardada = false
    private var categoria: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLlamadaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //instancia de FirebaseAuth y de la clase FusedLocationProviderClient de la API de ubicación de Google Play Services.
        firebaseAuth = FirebaseAuth.getInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        categoria = intent.getStringExtra("categoria") ?: "Categoría no disponible"

        //se obtiene una referencia a la ventana actual y se establece su tamaño con setLayout.
        val window = window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        configurarInterfaz()
        obtenerNumeroDeIntent()
        verificarYSolicitarPermisos()


    }

    override fun onResume() {
        //método onResume() de la superclase y luego se crea una instancia de la clase CallReceiver pasando la
        // actividad actual como argumento.
        super.onResume()
        callReceiver = CallReceiver(this)
        //se crea un objeto intentFilter utilizando el filtro de intención Telephony
        // Managerque se utiliza para detectar cambios de estado de la llamada telefónica, como si se contesta una llamada, se corta o se rechaza
        val intentFilter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        //El objeto intentFilter se utiliza para registrar callReceiver(devuelve una instancia de Broadcast receiver que puede obtener eventos
        // telefónicos relevantes) mediante registerReceiver().
        // Esto se utiliza para escuchar los cambios de estado de llamada telefónica y realizar alguna acción en consecuencia
        registerReceiver(callReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(callReceiver)
    }

    class CallReceiver(private val activity: LLamada) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                // La llamada ha terminado, obtenemos la información de la última llamada
                activity.mostrarInformacionUltimaLlamada(activity.ultimaLatitud, activity.ultimaLongitud)
            }
        }
    }


    private fun configurarInterfaz() {
        binding.IbCerrar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnCancelarLlamada.setOnClickListener {
            AlertDialog.Builder(this@LLamada)
                .setTitle("Confirmar")
                .setMessage("¿Estás seguro de que quieres cerrar?")
                .setPositiveButton("Sí") { _, _ -> finish() }
                .setNegativeButton("No", null)
                .show()
        }
        binding.BtnLLamar.setOnClickListener {
            // Implementación del botón de llamada
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Si ambos permisos están concedidos, realiza la llamada y obtiene las coordenadas
                realizarLlamadaYObtenerCoordenadas()
            } else {
                // Si no, solicita los permisos necesarios
                verificarYSolicitarPermisos()
            }

        }
    }

    private fun obtenerNumeroDeIntent() {
        numeroTelefonico = intent.getStringExtra("numeroTelefonico")
        /*val categoria = intent.getStringExtra("categoria") ?: "Categoría no disponible"
        binding.EtNombreOrganizacion.text = categoria*/
        binding.EtNombreOrganizacion.text = categoria
        binding.EtNumero.text = numeroTelefonico
    }

    private fun verificarYSolicitarPermisos() {
        val permisosNecesarios = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val listaPermisosFaltantes = permisosNecesarios.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (listaPermisosFaltantes.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listaPermisosFaltantes.toTypedArray(), REQUEST_PERMISSIONS_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Todos los permisos fueron concedidos
                    realizarLlamadaYObtenerCoordenadas()
                } else {
                    // Algunos permisos fueron denegados
                    for (index in permissions.indices) {
                        val permiso = permissions[index]
                        val resultado = grantResults[index]

                        if (resultado != PackageManager.PERMISSION_GRANTED) {
                            when (permiso) {
                                Manifest.permission.CALL_PHONE -> {
                                    mostrarExplicacion("Permiso de Llamada Telefónica", "Esta aplicación necesita el permiso de llamada para poder realizar llamadas telefónicas.")
                                }
                                Manifest.permission.READ_CALL_LOG -> {
                                    mostrarExplicacion("Permiso de Registro de Llamadas", "Esta aplicación necesita acceder al registro de llamadas para mostrar información sobre las llamadas realizadas.")
                                }
                                Manifest.permission.ACCESS_FINE_LOCATION -> {
                                    mostrarExplicacion("Permiso de Ubicación", "Esta aplicación necesita acceder a tu ubicación para ofrecer servicios basados en la ubicación.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun realizarLlamadaYObtenerCoordenadas() {
        numeroTelefonico?.let { numero ->
            // Verificar y solicitar permisos de llamada y ubicación
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // Realizar llamada telefónica
                val intentLlamada = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))
                startActivity(intentLlamada)

                // Obtener ubicación actual
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val latitud = it.latitude
                            val longitud = it.longitude
                            // Guardar o procesar la ubicación aquí
                            // Mostrar mensaje después de terminar la llamada
                            mostrarMensajeLlamadaTerminada(latitud, longitud)
                        }
                    }
                    .addOnFailureListener {
                        mostrarMensajeErrorUbicacion()
                    }
            } else {
                // Solicitar permisos si no están concedidos
                verificarYSolicitarPermisos()
            }
        }
    }
    @SuppressLint("Range")
    //funcion de informacion con la ubbicacion como parametros
    private fun mostrarInformacionUltimaLlamada(latitud: Double, longitud: Double) {
        //nuevamente se evalua permisos de lectura de informacion de llamada, si esto esta bien se hprocede a hjacer lectura de la ultima llamada.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
           //se crea un cursor que se utiliza para leer el registro de llamadas del dispositivo en orden descendente de fecha de llamada.
            val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC")
            cursor?.use {
                //Si se puede mover a la primera fila del cursor, se obtiene información sobre la última llamada, como el número, el tipo de llamada, la fecha, la duración,
                // el nombre del contacto y el estado.
                if (it.moveToFirst()) {
                    val numero = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                    val tipo = obtenerTipoLlamada(it.getInt(it.getColumnIndex(CallLog.Calls.TYPE)))
                    val fecha = Date(it.getLong(it.getColumnIndex(CallLog.Calls.DATE)))
                    val duracion = it.getString(it.getColumnIndex(CallLog.Calls.DURATION))
                    val nombreContacto = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
                    val estado = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION))
                    //Se muestra la información en la vista utilizando la función "mostrarDetallesLlamada()" y se pasa la información obtenida junto a
                    // la ubicación latitude y longitud.
                    mostrarDetallesLlamada(numero, tipo, fecha, duracion, nombreContacto, estado,latitud, longitud, categoria)

                } else {
                    mostrarMensajeError("No se pudo acceder al registro de llamadas.")
                }
            }
            //Si no se conceden los permisos necesarios, se llama a la función "mostrarExplicacion()" para informar al usuario
        // sobre la importancia del permiso y para explicar cómo se utiliza en la aplicación.
        } else {
            mostrarExplicacion("Permiso de Registro de Llamadas", "Esta aplicación necesita acceder al registro de llamadas para mostrar información sobre las llamadas realizadas.")
        }
    }

    private fun mostrarMensajeLlamadaTerminada(latitud: Double, longitud: Double) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Llamada Realizada")
            //Se establece el mensaje del cuadro de diálogo que incluye información sobre la llamada terminada, como la ubicación (latitud y longitud) donde se realizó la llamada. Esta información se
            // muestra en el mensaje mediante setMessage("La llamada ha terminado...\nLatitud: $latitud\nLongitud: $longitud").
            .setMessage("La llamada ha terminado.\nUbicación:\nLatitud: $latitud\nLongitud: $longitud")
            //Cuando el usuario presiona este botón, se ejecuta el bloque de código dentro de las llaves.
            .setPositiveButton("Ok") { dialog, _ ->
                //Se llama a la función mostrarInformacionUltimaLlamada(latitud, longitud) para mostrar información adicional sobre la
                // última llamada realizada con la ubicación proporcionada.
                ubicacionMostrada = true
                mostrarInformacionUltimaLlamada(latitud, longitud)


            }
                //el usuario no puede cerrarlo haciendo clic fuera del cuadro de diálogo.
            .setCancelable(false)
            .create()
        dialog.setOnDismissListener {
            ubicacionMostrada = true
            //mostrarInformacionUltimaLlamada()
        }

        dialog.show()

    }

    private fun mostrarMensajeErrorUbicacion() {
        AlertDialog.Builder(this)
            .setTitle("Error de Ubicación")
            .setMessage("No se pudo obtener la ubicación actual.")
            .setPositiveButton("Aceptar", null)
            .show()
            .setCancelable(false)

    }
    private fun mostrarExplicacion(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Entendido", null)
            .show()
            .setCancelable(false)

    }


    private var ultimaLatitud: Double = 0.0
    private var ultimaLongitud: Double = 0.0
    data class LlamadaData(
        val numero: String,
        val tipo: String,
        val fecha: String, // Ahora es un String
        val duracion: String,
        val nombreContacto: String,
        val estado: String,
        val latitud: Double,
        val longitud: Double,
        val categoria: String
    )

    //parametros orientados a la llamada
    private fun mostrarDetallesLlamada(numero: String, tipo: String, fecha: Date, duracion: String, nombreContacto: String, estado: String, latitud: Double, longitud: Double, categoria: String) {
        val formatoFecha = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val fechaFormateada = formatoFecha.format(fecha)
        //variable para definir un mensaje de dialogo
        val mensaje = "Categoría: $categoria\nNúmero: $numero\nTipo: $tipo\nFecha: $fechaFormateada\nDuración: $duracion segundos\n" +
                "NombreContacto: $nombreContacto\nUbicacion: $estado\n" +
                "Latitud: $latitud\n" +
                "Longitud: $longitud"
        //al mantener solo lo que es opcion de acpetar se garantiza que solo si se da en aceptar so comenzara a registrar la infromacion en la base de datos
        val dialog = AlertDialog.Builder(this)
            .setTitle("Detalles de la Última Llamada")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .create()
        dialog.setCancelable(false)
        //una vez que se ha dado en aceptar se genera un nuevo mensaje donde avisa que se estara guardando dicha informacion en el mensaje
        dialog.setOnDismissListener {
            // Aquí guardamos los datos en Firebase
            guardarDatosLlamadaEnFirebase(numero, tipo, fecha, duracion, nombreContacto, estado, latitud, longitud, categoria)
        }
           dialog.setCancelable(false)

        dialog.show()
    }
    //parametros para la funcion guardar informacion en firebase
    private fun guardarDatosLlamadaEnFirebase(numero: String, tipo: String, fecha: Date, duracion: String, nombreContacto: String, estado: String, latitud: Double, longitud: Double,categoria: String) {
        val uid = firebaseAuth.currentUser?.uid

        if (uid != null && uid.isNotEmpty()) {
            //se establece el formato
            val formatoFecha = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
            //se asigna el valor de fecha formateada a lo quwe es fechacha hasta entonces valuada en milisegundos
            val fechaFormateada = formatoFecha.format(fecha)
            //en la variable llamadaData sasignamos los parametros que venimos manejando
            val llamadaData = LlamadaData(numero, tipo, fechaFormateada, duracion, nombreContacto, estado, latitud, longitud, categoria)
            //se genera la ruta de
            val databaseRef = FirebaseDatabase.getInstance().getReference("Llamadas").child(uid)

            databaseRef.push().setValue(llamadaData)
                .addOnSuccessListener {
                    // Datos guardados con éxito
                   // Toast.makeText(this,"A continuacion recibira un mensaje para que pueda verificar su ubicacion",
                    //    Toast.LENGTH_SHORT).show()
                    AlertDialog.Builder(this)
                        .setTitle("Confirmación")
                        .setMessage("A continuación recibirá un mensaje para que pueda verificar su ubicación.")
                        .setPositiveButton("Aceptar") { dialog, which ->
                            // Acción a realizar cuando se presiona "Aceptar"

                            // Por ejemplo, cerrar el diálogo
                            dialog.dismiss()

                            mostrarNotificacionDeConfirmacion()

                        }
                        .show()
                }
                .addOnFailureListener { exception ->
                    // Error al guardar datos
                    Log.e("FirebaseError", "Error al guardar datos de llamada", exception)
                    // Opcional: Mostrar un mensaje al usuario
                }
        } else {
            // Usuario no autenticado o UID no disponible
            Log.e("FirebaseError", "Usuario no autenticado o UID no disponible")
            // Opcional: Mostrar un mensaje al usuario
        }
    }


    private fun obtenerTipoLlamada(tipo: Int): String {
        return when (tipo) {
            CallLog.Calls.OUTGOING_TYPE -> "Saliente"
            CallLog.Calls.INCOMING_TYPE -> "Entrante"
            CallLog.Calls.MISSED_TYPE -> "Perdida"
            else -> "Desconocido"
        }
    }

    private fun mostrarMensajeError(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
            .setCancelable(false)
    }

    private fun mostrarNotificacionDeConfirmacion() {
        val delayMillis: Long = 2000 // Retraso de 2 segundos

        Handler(Looper.getMainLooper()).postDelayed({
            val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager

            val notificationId = 1
            val channelId = "channel_id"
            val channelName = "Channel Name"

            // Intent para realizar la acción cuando se toque el botón de la notificación
            val intentAccion = Intent(this, locationActivity::class.java) // Ajusta DestinoActivity a tu actividad destino
            val pendingIntent = PendingIntent.getActivity(this,
                0,
                intentAccion,
                PendingIntent.FLAG_UPDATE_CURRENT
                        or PendingIntent.FLAG_IMMUTABLE)

            // Crear el canal de notificación para Android 8.0 y versiones superiores
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(channel)
            }

            // Construir la notificación incluyendo el botón de acción
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Establecer un icono para la notificación
                .setContentTitle("Verificar Ubicación") // Título de la notificación
                .setContentText("Comprueba por ti mismo tu ubicación.") // Texto de la notificación
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Establecer la prioridad
                .setAutoCancel(true) // Hacer que la notificación desaparezca al ser tocada
                .addAction(android.R.drawable.ic_menu_view, "Buscar", pendingIntent) // Agregar botón con la acción

            // Emitir la notificación
            notificationManager.notify(notificationId, notificationBuilder.build())
        }, delayMillis)
    }


}