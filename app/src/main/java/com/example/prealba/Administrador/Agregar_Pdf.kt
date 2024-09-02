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
import com.example.prealba.Modelos.ModeloCategoria
import com.example.prealba.databinding.ActivityAgregarPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Agregar_Pdf : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarPdfBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoriaArrayList: ArrayList<ModeloCategoria>
    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        CargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.AdjuntarPdfIb.setOnClickListener {
            /* if (ContextCompat.checkSelfPermission(this@Agregar_Pdf,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                elegirPdf()
            }else{
                SolicitudPermisoAccederArchivos.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }*/
            solicitarPermisoStorage()
        }

        binding.TvCategoriaLibro.setOnClickListener {
            SeleccionarCat()
        }

        binding.BtnSubirArchivo.setOnClickListener {
            ValidarInformacion()
        }


    }

    private var titulo = ""
    private var descripcion = ""
    private var categoria = ""

    private fun ValidarInformacion() {
        titulo = binding.EtTituloLibro.text.toString().trim()
        descripcion = binding.EtDescripcionLibro.text.toString().trim()
        categoria = binding.TvCategoriaLibro.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingrese titulo", Toast.LENGTH_SHORT).show()
        } else if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingrese descripcion", Toast.LENGTH_SHORT).show()
        } else if (categoria.isEmpty()) {
            Toast.makeText(this, "Seleccione categoria", Toast.LENGTH_SHORT).show()

        } else if (pdfUri == null) {
            Toast.makeText(this, "Adjunte un archivo", Toast.LENGTH_SHORT).show()

        } else {
            SubirPdfStore()
        }
    }

    private fun SubirPdfStore() {
        //implementar firestore
        //subir pdf en apartado de storage
        progressDialog.setMessage("Subiendo Archivo")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()
        val ruta_libro = "Libros/$tiempo"
        val storageReference = FirebaseStorage.getInstance().getReference(ruta_libro)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { tarea ->
                val uriTask: Task<Uri> = tarea.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val UrlPdfSubido = "${uriTask.result}"
                SubirPdfBD(UrlPdfSubido, tiempo)

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo la carga del archivo debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()


            }
    }

    private fun SubirPdfBD(urlPdfSubido: String, tiempo: Long) {
        progressDialog.setMessage("Subiendo el archivo a la Base de Datos")
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$tiempo"
        hashMap["titulo"] = titulo
        hashMap["descripcion"] = descripcion
        hashMap["categoria"] = id_categoria
        hashMap["url"] = urlPdfSubido
        hashMap["tiempo"] = tiempo
        hashMap["contadorVistas"] = 0
        hashMap["contadorDescargas"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "El archivo se subió con exito ", Toast.LENGTH_SHORT).show()
                binding.EtTituloLibro.setText("")
                binding.EtDescripcionLibro.setText("")
                binding.TvCategoriaLibro.setText("")
                pdfUri = null


            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo la carga del archivo debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }


    }

    private fun CargarCategorias() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categoria").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var id_categoria = ""
    private var titulo_categoria = ""

    private fun SeleccionarCat() {

        //Se crea un arreglo (categoriasArray) de tipo String con la misma longitud que la lista categoriaArrayList.
        // Este arreglo se utilizará para almacenar los nombres de las categorías.
        val categoriasArray = arrayOfNulls<String>(categoriaArrayList.size)

        //Se recorre la lista categoriaArrayList y se asigna el nombre de cada categoría al arreglo categoriasArray
        // en la posición correspondiente.
        for (i in categoriasArray.indices) {
            categoriasArray[i] = categoriaArrayList[i].categoria
        }
        //Se crea un constructor de AlertDialog utilizando el contexto de la actividad actual (this).
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar categoria")
            .setItems(categoriasArray) { dialog, which ->
                // Cuando se selecciona una categoría, se obtiene el ID de la categoría correspondiente desde la lista
                id_categoria = categoriaArrayList[which].id
                //se obtiene el nombre de la categoría seleccionada y se asigna a la variable
                titulo_categoria = categoriaArrayList[which].categoria
                binding.TvCategoriaLibro.text = titulo_categoria

            }
            .show()
    }

    /*
    private fun solicitarPermisoStorage() {
        // Comprobar la versión de Android del dispositivo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Para versiones anteriores a Android 10 (API nivel 29)
            // Verifica si el permiso de almacenamiento externo ya está concedido
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Si no está concedido, solicitar el permiso
                SolicitudPermisoAccederArchivos.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private val SolicitudPermisoAccederArchivos = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, proceder a elegir PDF
            elegirPdf()
        } else {
            // Permiso denegado, mostrar mensaje al usuario
            Toast.makeText(this, "El permiso de acceso a almacenamiento externo ha sido denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun elegirPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }
        pdfActivityRL.launch(intent)
    }

    val pdfActivityRL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            pdfUri = resultado.data?.data
            // Procede aquí con la manipulación de la URI del PDF seleccionado
        } else {
            Toast.makeText(this, "Selección de PDF cancelada", Toast.LENGTH_SHORT).show()
        }
    }

     */
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
            pdfUri = resultado.data?.data
        } else {
            // Se ha cancelado la selección del archivo, mostrar mensaje al usuario
            Toast.makeText(this, "Selección de PDF cancelada", Toast.LENGTH_SHORT).show()
        }
    }


}