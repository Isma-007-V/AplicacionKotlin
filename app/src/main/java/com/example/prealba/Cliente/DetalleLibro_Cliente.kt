package com.example.prealba.Cliente

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.prealba.Administrador.Constantes
import com.example.prealba.Administrador.MisFunciones
import com.example.prealba.LeerLibro
import com.example.prealba.Modelos.ModeloComentario
import com.example.prealba.R
import com.example.prealba.databinding.ActivityDetalleLibroClienteBinding
import com.example.prealba.databinding.DialogAgregarComentarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class DetalleLibro_Cliente : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleLibroClienteBinding

    private var idLibro = ""
    private var tituloLibro = ""
    private var urlLibro = ""

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var esFavorito = false

    private lateinit var comentarioArrayList : ArrayList<ModeloComentario>
    private lateinit var adaptadorComentario: AdaptadorComentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        MisFunciones.incrementarVistas(idLibro)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Un momento por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnLeerLibroC.setOnClickListener {
            val intent = Intent(this@DetalleLibro_Cliente, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        binding.BtnDescargarLibroC.setOnClickListener {

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
                descargarLibro()
            }else{
                permisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.BtnFavoritosLibroC.setOnClickListener{
            if(esFavorito){
                MisFunciones.eliminarFavoritos(this@DetalleLibro_Cliente, idLibro)
            }else{
                agregarFavoritos()
            }
        }

        binding.IbAgregarComentario.setOnClickListener{
            dialogComentar()
        }

        comrpobarFavorito()
        //funcion para ver lso detalles del documento.
        cargarDetalles()
        listarComentarios()

    }

    private fun listarComentarios() {
        comentarioArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro).child("Comentarios")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    comentarioArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloComentario::class.java)
                        comentarioArrayList.add(modelo!!)
                    }
                    adaptadorComentario = AdaptadorComentario(this@DetalleLibro_Cliente, comentarioArrayList)
                    binding.RvComentarios.adapter = adaptadorComentario
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    private var comentario = ""
    private fun dialogComentar() {
        //Luego se crea un objeto AlertDialog.Builder y se establece la vista del diálogo.
        val agregar_com_binding = DialogAgregarComentarioBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
        builder.setView(agregar_com_binding.root)

        val alertDialog = builder.create()
        alertDialog.show()
        //e muestra el diálogo utilizando alertDialog.show() y se establece setCanceledOnTouchOutside como false para evitar que el diálogo se
        // cierre si el usuario toca fuera del diálogo.
        alertDialog.setCanceledOnTouchOutside(false)
        //Se define un listener de clic para el botón "Cerrar" que se utiliza para cerrar el cuadro de diálogo.
        agregar_com_binding.IbCerrar.setOnClickListener{
            alertDialog.dismiss()
        }
        //También se establece un listener de clic para el botón "Comentar" que se utiliza para obtener el texto del comentario introducido
        // por el usuario en el cuadro de texto EtAgregarComentario.
        agregar_com_binding.BtnComentar.setOnClickListener{
            comentario = agregar_com_binding.EtAgregarComentario.text.toString().trim()
            //Si no se introduce ningún comentario, se muestra un mensaje de advertencia al usuario. De lo contrario, se cierra el cuadro
            // de diálogo y se llama a la función agregarComentario() para agregar el comentario a la base de datos.
            if (comentario.isEmpty()){
                Toast.makeText(applicationContext, "Agregar comentario", Toast.LENGTH_SHORT).show()

            }else {
                alertDialog.dismiss()
                agregarComentario()
            }
        }
    }

    private fun agregarComentario() {
        progressDialog.setMessage("Agregar comentario")
        progressDialog.show()

        val tiempo = "${System.currentTimeMillis()}"

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["idLibro"] = "${idLibro}"
        hashMap["tiempo"] = "$tiempo"
        hashMap["comentario"] = "${comentario}"
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro).child("Comentarios").child(tiempo)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Su comentario se ha publicado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun comrpobarFavorito() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    esFavorito = snapshot.exists()
                    if(esFavorito){
                        binding.BtnFavoritosLibroC.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_agregar_favoritos,
                            0,
                            0
                        )
                        //setear el siguiente mensaje
                        binding.BtnFavoritosLibroC.text ="Eliminar"
                    }else{
                        binding.BtnFavoritosLibroC.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_no_favoritos,
                            0,
                            0
                        )
                        binding.BtnFavoritosLibroC.text ="Agregar"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun agregarFavoritos(){
        //Obtiene el tiempo actual en milisegundos utilizando
        val tiempo = System.currentTimeMillis()
        //configurar datos que van a database
        //Configura un HashMap (hashMap) que contiene dos pares clave-valor: "id" (que se establece como el ID del libro) y
        // "tiempo" (que se establece como el tiempo actual en milisegundos).
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = idLibro
        hashMap["tiempo"] = tiempo
        //Obtiene una referencia a la base de datos Firebase utilizando FirebaseDatabase.getInstance().getReference("Usuarios").
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        //accede al nodo "Favoritos" y agrega un nuevo nodo con el ID del libro (idLibro) como clave y el HashMap hashMap como valor.
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .setValue(hashMap)
            .addOnSuccessListener {
                //i la operación de agregar el documento a favoritos tiene éxito, muestra un mensaje de éxito mediante un Toast.
                Toast.makeText(applicationContext, "Docuento Agregado con exito", Toast.LENGTH_SHORT).show()

            }
            //Si la operación falla, muestra un mensaje de error que indica la razón del fallo, como problemas de conexión o permisos insuficientes.
            .addOnFailureListener{e->
                Toast.makeText(applicationContext, "No se agrego a favoritos debido a " + "${e.message}", Toast.LENGTH_SHORT).show()

            }
    }


    private fun descargarLibro() {
        progressDialog.setMessage("descargando documento")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
        storageReference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener { bytes ->
                guardarLibroDispositivo(bytes)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun guardarLibroDispositivo(bytes: ByteArray) {
        //varias veces descargar
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
            incrementarNumDesc()
            
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()

        }
    }

    private fun cargarDetalles() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener la informacion del libro a traves del id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    tituloLibro = "${snapshot.child("titulo").value}"
                    urlLibro = "${snapshot.child("url").value}"


                    //formato de tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar categoria
                    MisFunciones.CargarCategoria(categoria, binding.cetegoriaD)
                    //cargare la miniatura de libro contador pag.
                    MisFunciones.CargarPdfUrl(
                        "$urlLibro", "$tituloLibro", binding.VisualizadorPDF, binding.progressBar,
                        binding.paginasD
                    )
                    MisFunciones.CargarTamanioPdf("$urlLibro", "$tituloLibro", binding.tamanioD)

                    //sentamos la informacionb restante
                    binding.tituloLibroD.text = tituloLibro
                    binding.descripcionD.text = descripcion
                    binding.vistasD.text = contadorVistas
                    binding.descargasD.text = contadorDescargas
                    binding.fechaD.text = fecha
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    //recordatorio, para descarga de libros o informacion favor de crear funcion en este archivo
    private fun incrementarNumDesc() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
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

                    val BDRef = FirebaseDatabase.getInstance().getReference("Libros")
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
            descargarLibro()
        } else {
            Toast.makeText(applicationContext, "El permiso de almacenmamiento a sido denegado",
                Toast.LENGTH_SHORT).show()
        }

    }
}



