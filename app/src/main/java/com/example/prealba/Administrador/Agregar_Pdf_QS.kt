package com.example.prealba.Administrador

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.prealba.databinding.ActivityAgregarPdfQsBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Agregar_Pdf_QS : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarPdfQsBinding
    private lateinit var categoriaArrayList: ArrayList<ModeloCategoriaQS>
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    private var pdfUriQS : Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPdfQsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        CargarCategoriasQS()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.AdjuntarPdfIbQS.setOnClickListener{
            solicitarPermisoStorage()
        }
        binding.BtnSubirArchivoORQS.setOnClickListener{
          ValidarInformacionQS()
        }

        binding.TvCategoriaLibroORGQS.setOnClickListener{
            SeleccionarCatQS()
        }

    }
    private var titulo = ""
    private var objetivo = ""
    private var qhacemos = ""
    private var telefono = ""
    private var categoria = ""
    private fun ValidarInformacionQS() {
        titulo = binding.EtTituloOrQS.text.toString().trim()
        objetivo = binding.EtObjetivoOrLibroQS.text.toString().trim()
        qhacemos = binding.EtAccionesOrLibroQS.text.toString().trim()
        telefono = binding.EtTelefonoOrLibroQS.text.toString().trim()
        categoria = binding.TvCategoriaLibroORGQS.text.toString().trim()

        //implementacion de las condficionales.

        if(titulo.isEmpty()){
            Toast.makeText(this, "Ingrese titulo", Toast.LENGTH_SHORT).show()
        }
        else if (objetivo.isEmpty()){
            Toast.makeText(this, "Ingrese objetivo", Toast.LENGTH_SHORT).show()
        }
        else if (qhacemos.isEmpty()){
            Toast.makeText(this, "Ingrese Acciones", Toast.LENGTH_SHORT).show()
        }
        else if (telefono.isEmpty()){
            Toast.makeText(this, "Ingrese numero telefonico", Toast.LENGTH_SHORT).show()
        }

        else if (categoria.isEmpty()){
            Toast.makeText(this, "Seleccione categoria", Toast.LENGTH_SHORT).show()

        }
        else if (pdfUriQS==null){
            Toast.makeText(this, "Adjunte un archivo informativo", Toast.LENGTH_SHORT).show()
        }
        else{
            SubirPdfStoreQS()
        }
    }
        //funcion para subir un pdf o archivo.
    private fun SubirPdfStoreQS() {
            //implementar firestore
            //subir pdf en apartado de storage
            progressDialog.setMessage("Subiendo Archivo")
            progressDialog.show()

            val tiempo = System.currentTimeMillis()
            val ruta_libro="LibrosQS/$tiempo"
            val storageReference = FirebaseStorage.getInstance().getReference(ruta_libro)
            storageReference.putFile(pdfUriQS!!)
                .addOnSuccessListener {tarea->
                    val uriTask : Task<Uri> = tarea.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val UrlPdfSubido = "${uriTask.result}"
                    SubirPdfBDQS(UrlPdfSubido, tiempo)

                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Fallo la carga del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()


                }
    }

    private fun SubirPdfBDQS(urlPdfSubido: String, tiempo: Long) {
        progressDialog.setMessage("Subiendo el archivo a la Base de Datos")
        val uid = firebaseAuth.uid

        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["uid"]= "$uid"
        hashMap["id"]="$tiempo"
        hashMap["titulo"]= titulo
        hashMap["objetivo"]= objetivo
        hashMap["acciones"]= qhacemos
        hashMap["telefono"] = telefono
        hashMap["categoria"]= id_categoriaQS
        hashMap["url"]= urlPdfSubido
        hashMap["tiempo"]= tiempo
        hashMap["contadorVistas"]= 0
        hashMap["contadorDescargas"]= 0

        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "El archivo se subió con exito ", Toast.LENGTH_SHORT).show()
                binding.EtTituloOrQS.setText("")
                binding.EtObjetivoOrLibroQS.setText("")
                binding.EtAccionesOrLibroQS.setText("")
                binding.EtTelefonoOrLibroQS.setText("")
                binding.TvCategoriaLibroORGQS.setText("")
                pdfUriQS = null



            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo la carga del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

    private fun CargarCategoriasQS() {
        categoriaArrayList= ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("CategoriaQS").orderByChild("categoria")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for(ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoriaQS::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var id_categoriaQS=""
    private var titulo_categoriaQS= ""

    private fun SeleccionarCatQS(){
        val categoriasArray = arrayOfNulls<String>(categoriaArrayList.size)
        for (i in categoriasArray.indices){
            categoriasArray[i] = categoriaArrayList[i].categoria
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar categoria")
            .setItems(categoriasArray){dialog, which->
                id_categoriaQS = categoriaArrayList[which].id
                titulo_categoriaQS= categoriaArrayList[which].categoria
                binding.TvCategoriaLibroORGQS.text = titulo_categoriaQS

            }
            .show()
    }
    /*
    private fun ElegirPdfQS(){
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityRLQS.launch(intent)

    }
    val pdfActivityRLQS = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado->
            if(resultado.resultCode == RESULT_OK){
                pdfUriQS = resultado.data!!.data
            }else{
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )
    private val SolicitudPermisoAccederArchivos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso->
            if(Permiso){
                ElegirPdfQS()
            }else {
                Toast.makeText(this, "El permiso ha sido denegado", Toast.LENGTH_SHORT).show()

            }
        }*/
    private fun solicitarPermisoStorage() {
        // Comprobar la versión de Android del dispositivo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Para versiones anteriores a Android 10 (API nivel 29)
            // Verifica si el permiso de almacenamiento externo ya está concedido
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Si no está concedido, solicitar el permiso y mostrar un mensaje explicando por qué se necesita
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )) {
                    AlertDialog.Builder(this)
                        .setTitle("Se necesita acceso al almacenamiento externo")
                        .setMessage("La aplicación necesita acceder al almacenamiento externo para adjuntar archivos PDF.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            SolicitudPermisoAccederArchivos.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                } else {
                    SolicitudPermisoAccederArchivos.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // El permiso ya está concedido, proceder a elegir PDF
                elegirPdf()
            }
        } else {
            // Para Android 10 (API nivel 29) y versiones superiores, el acceso a archivos
            // a través de la acción ACTION_GET_CONTENT no requiere permisos de almacenamiento.
            // Se puede proceder directamente a elegir un PDF.
            elegirPdf()
        }
    }

    private fun elegirPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }
        pdfActivityRL.launch(intent)
    }

    private val SolicitudPermisoAccederArchivos = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, proceder a elegir PDF
            elegirPdf()
        } else {
            // Permiso denegado, mostrar mensaje al usuario
            Toast.makeText(this, "El permiso de acceso a almacenamiento externo ha sido denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val pdfActivityRL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            // Se ha seleccionado un archivo PDF, obtener la URI
            pdfUriQS = resultado.data?.data
        } else {
            // Se ha cancelado la selección del archivo, mostrar mensaje al usuario
            Toast.makeText(this, "Selección de PDF cancelada", Toast.LENGTH_SHORT).show()
        }
    }

}