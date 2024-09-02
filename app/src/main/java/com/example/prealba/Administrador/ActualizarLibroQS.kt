package com.example.prealba.Administrador

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.databinding.ActivityActualizarLibroQsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarLibroQS : AppCompatActivity() {
    private lateinit var binding : ActivityActualizarLibroQsBinding
    private var idLibro = ""
    private lateinit var  progressDialog: ProgressDialog
    //titulos
    private lateinit var catTituloArrayList: ArrayList<String>

    //id
    private lateinit var catIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarLibroQsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarCategoriasQS()
        cargarInformacionQS()
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.TvCategoriaLibroORGQS.setOnClickListener{
            dialogoCategoriaQS()
        }
        binding.BtnActualizarArchivoQs.setOnClickListener{
            validarinformacionQS()

        }

    }

    private fun cargarInformacionQS() {
        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener la informacion en tiempo real del libro seleccionado
                    val titulo = snapshot.child("titulo").value.toString()
                    val objetivo = snapshot.child("objetivo").value.toString()
                    val acciones = snapshot.child("acciones").value.toString()
                    val telefono = snapshot.child("telefono").value.toString()

                    id_seleccionado= snapshot.child("categoria").value.toString()

                    binding.EtTituloOrQS.setText(titulo)
                    binding.EtObjetivoOrLibroQS.setText(objetivo)
                    binding.EtAccionesOrLibroQS.setText(acciones)
                    binding.EtTelefonoOrLibroQS.setText(telefono)

                    val refCategoria = FirebaseDatabase.getInstance().getReference("CategoriaQS")
                    refCategoria.child(id_seleccionado)
                        .addListenerForSingleValueEvent(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //obtener categoria
                                val categoria = snapshot.child("categoria").value
                                binding.TvCategoriaLibroORGQS.text= categoria.toString()

                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
//inicializacion de vaRIABLES
    private var titulo = ""
    private var objetivo = ""
    private var qhacemos = ""
    private var telefono = ""
    //evaluacion de varibales
    private fun validarinformacionQS() {
        titulo = binding.EtTituloOrQS.text.toString().trim()
        objetivo = binding.EtObjetivoOrLibroQS.text.toString().trim()
        qhacemos = binding.EtAccionesOrLibroQS.text.toString().trim()
        telefono = binding.EtTelefonoOrLibroQS.text.toString().trim()

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
        else if (id_seleccionado.isEmpty()){
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT).show()

        }else{
            actualizarInformacionQS()
        }

    }
    //funcion para actualizar variables y guradarlas en firebase
    private fun actualizarInformacionQS() {
        //Primero, muestra un diálogo de progreso con un mensaje indicando que se está actualizando la información del libro.
        progressDialog.setMessage("Actualizando informacion")
        progressDialog.show()
        // través del uso de un HashMap, se actualizan los campos "titulo", "objetivo", "acciones", "telefono" y "categoria"
        // con los valores proporcionados.
        val hashMap = HashMap<String, Any>()
        hashMap["titulo"] = "$titulo"
        hashMap["objetivo"]= "$objetivo"
        hashMap["acciones"]= "$qhacemos"
        hashMap["telefono"]= "$telefono"
        hashMap["categoria"] = "$id_seleccionado"
        //Se obtiene la referencia a la base de datos a través del método getReference y se actualiza el libro específico
        // utilizando el id del libro.
        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        //Si la actualización es exitosa, se cierra el diálogo de progreso y se muestra un mensaje indicando que la operación fue exitosa.
        ref.child(idLibro)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "La actualizacion fue exitosa ", Toast.LENGTH_SHORT).show()

            }
            //caso contrario
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "La actualizacion fallo debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }


    private  var id_seleccionado =""
    private var titulo_seleccionado = ""
    private fun dialogoCategoriaQS() {
        val categoriaArray = arrayOfNulls<String>(catTituloArrayList.size)
        for(i in catTituloArrayList.indices){
            categoriaArray[i] = catTituloArrayList[i]
        }
        //dialogo de alerta
        val buider = AlertDialog.Builder(this)
        buider.setTitle("seleccione una categoria")
            .setItems(categoriaArray){dialog, posicion->
                id_seleccionado = catIdArrayList[posicion]
                titulo_seleccionado = catTituloArrayList[posicion]

                binding.TvCategoriaLibroORGQS.text = titulo_seleccionado
            }
            .show()
    }

    private fun cargarCategoriasQS() {
        catTituloArrayList = ArrayList()
        catIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("CategoriaQS")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catTituloArrayList.clear()
                catIdArrayList.clear()
                for(ds in snapshot.children){
                    val id= ""+ds.child("id")
                    val categoria= ""+ds.child("categoria").value

                    catTituloArrayList.add(categoria)
                    catIdArrayList.add(id)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}