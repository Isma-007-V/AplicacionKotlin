package com.example.prealba.Administrador

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.LeerLibroQS
import com.example.prealba.databinding.ActivityDetalleLibroQsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Detalle_Libro_QS : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleLibroQsBinding
    private var idLibro = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroQsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLeerLibroQS.setOnClickListener{
            val intent = Intent(this@Detalle_Libro_QS, LeerLibroQS::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }
        cargarDetalleLibroQS()
    }
    //carga la información detallada de un libro específico de la base de datos de Firebase.
    private fun cargarDetalleLibroQS() {
        // Utilizando el id del libro proporcionado, se obtiene una referencia a través del método getReference
        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                //se obtienen los valores de las diferentes propiedades del libro (categoría, contadorDescargas, contadorVistas, qhacemos,
                // tiempo, titulo y url) y se las setea en los diferentes objetos del diseño de la pantalla.
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener la informacion del libro a traves del id
                    val categoria  = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val qhacemos = "${snapshot.child("acciones").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val titulo = "${snapshot.child("titulo").value}"
                    val url = "${snapshot.child("url").value}"

                    //MisFunciones.CargarCategoriaQS se llama para mostrar la categoría, MisFunciones.CargarPdfUrl se llama para cargar la miniatura del
                    // libro y las MisFunciones.CargarTamanioPdf se llama para cargar el tamaño del archivo PDF.
                    //formato de tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar categoria
                    MisFunciones.CargarCategoriaQS(categoria, binding.cetegoriaDQS)
                    //cargare la miniatura de libro contador pag.
                    MisFunciones.CargarPdfUrl("$url", "$titulo", binding.VisualizadorPDFQS, binding.progressBarQS,
                        binding.paginasDQS)
                    MisFunciones.CargarTamanioPdf("$url", "$titulo", binding.tamanioDQS)

                    //sentamos la informacionb restante
                    binding.tituloLibroDQS.text = titulo
                    binding.AccionesDQS.text = qhacemos
                    binding.vistasDQS.text = contadorVistas
                    binding.descargasDQS.text = contadorDescargas
                    binding.fechaDQQS.text= fecha
                }
                //Si ocurre algún error al cargar la información, se ejecuta el método "onCancelled" pero en este caso no se hace nada.
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}