package com.example.prealba.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.prealba.Administrador.Constantes
import com.example.prealba.Administrador.MisFunciones
import com.example.prealba.LeerLibroQS
import com.example.prealba.databinding.ActivityDetalleLibroClienteQsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class DetalleLibro_ClienteQS : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLibroClienteQsBinding
    private var idLibro = ""

    private var tituloLibro = ""
    private var urlLibro = ""
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroClienteQsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        MisFunciones.incrementarVistasQSCL(idLibro)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Un momento por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnLeerLibroQS.setOnClickListener {
            val intent = Intent(this@DetalleLibro_ClienteQS, LeerLibroQS::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }
        binding.BtnDescargarLibroCQS.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
                descargarLibroQSCL()
            }else{
                permisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        cargarDetallesQSC()
    }

    private fun descargarLibroQSCL() {
        progressDialog.setMessage("descargando documento")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
        storageReference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener { bytes ->
                guardarLibroDispositivoQSC(bytes)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarLibroDispositivoQSC(bytes: ByteArray?) {
        val nombreLibro_extension = "$tituloLibro"+System.currentTimeMillis()+".pdf"
        try {
            val carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            carpeta.mkdirs()

            val archivo_ruta = carpeta.path + "/" + nombreLibro_extension
            val out = FileOutputStream(archivo_ruta)
            out.write(bytes)
            out.close()

            Toast.makeText(applicationContext, "Libro guardado con exito", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
            incrementarNumDescQSCL()

        } catch (e: Exception) {
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()

        }
    }

    private fun cargarDetallesQSC() {
        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener la informacion del libro a traves del id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val qhacemos = "${snapshot.child("acciones").value}"
                    val objetivo = "${snapshot.child("objetivo").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    tituloLibro = "${snapshot.child("titulo").value}"
                    urlLibro = "${snapshot.child("url").value}"


                    //formato de tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar categoria
                    MisFunciones.CargarCategoriaQS(categoria, binding.cetegoriaDQS)
                    //cargare la miniatura de libro contador pag.
                    MisFunciones.CargarPdfUrl(
                        "$urlLibro", "$tituloLibro", binding.VisualizadorPDFQS, binding.progressBarQS,
                        binding.paginasDQS
                    )
                    MisFunciones.CargarTamanioPdf("$urlLibro", "$tituloLibro", binding.tamanioDQS)

                    //sentamos la informacionb restante
                    binding.tituloLibroDQS.text = tituloLibro
                    binding.AccionesDQS.text = qhacemos
                    binding.ObjetivosDQS.text = objetivo
                    binding.TelefonoDQS.text = telefono
                    binding.vistasDQS.text = contadorVistas
                    binding.descargasDQS.text = contadorDescargas
                    binding.fechaDQQS.text = fecha
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun incrementarNumDescQSCL() {
        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var contDescarActual = "${snapshot.child("contadorDescargas").value}"

                    if(contDescarActual == "" || contDescarActual == "null"){
                        contDescarActual = "0"
                    }
                    val nuevaDes = contDescarActual.toLong() + 1

                    val hashMap = HashMap<String, Any>()
                    hashMap["contadorDescargas"] = nuevaDes

                    val BDRef = FirebaseDatabase.getInstance().getReference("LibrosQS")
                    BDRef.child(idLibro)
                        .updateChildren(hashMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    private val permisoAlmacenamiento = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { permiso_concedido ->
        if (permiso_concedido) {
            descargarLibroQSCL()
        } else {
            Toast.makeText(applicationContext, "El permiso de almacenmamiento a sido denegado",
                Toast.LENGTH_SHORT).show()
        }

    }
}