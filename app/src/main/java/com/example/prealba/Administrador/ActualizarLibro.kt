package com.example.prealba.Administrador

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.databinding.ActivityActualizarLibroBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarLibro : AppCompatActivity() {
    private lateinit var binding : ActivityActualizarLibroBinding

    //obtener idenviado desde adaptador.
    private var idLibro = ""

    private lateinit var  progressDialog: ProgressDialog

    //titulos
    private lateinit var catTituloArrayList: ArrayList<String>

    //id
    private lateinit var catIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarCategorias()
        cargarInformacion()

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.TvCategoriaLibro.setOnClickListener{
            dialogoCategoria()
        }
        binding.BtnActualizarLibro.setOnClickListener{
            validarinformacion()

        }


    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener la informacion en tiempo real del libro seleccionado
                    val titulo = snapshot.child("titulo").value.toString()
                    val descripcion = snapshot.child("descripcion").value.toString()
                    id_seleccionado= snapshot.child("categoria").value.toString()

                    binding.EtTituloLibro.setText(titulo)
                    binding.EtDescripcionLibro.setText(descripcion)

                    val refCategoria = FirebaseDatabase.getInstance().getReference("Categoria")
                    refCategoria.child(id_seleccionado)
                        .addListenerForSingleValueEvent(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //obtener categoria
                                val categoria = snapshot.child("categoria").value
                                binding.TvCategoriaLibro.text= categoria.toString()

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

    private var titulo = ""
    private var descripcion = ""

    private fun validarinformacion() {
        titulo = binding.EtTituloLibro.text.toString().trim()
        descripcion = binding.EtDescripcionLibro.text.toString().trim()

        if(titulo.isEmpty()){
           Toast.makeText(this, "Ingrese titulo", Toast.LENGTH_SHORT).show()
        }
        else if (descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese descripcion", Toast.LENGTH_SHORT).show()

        }
        else if (id_seleccionado.isEmpty()){
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT).show()

        }else{
            actualizarInformacion()
        }

    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizando informacion")
        progressDialog.show()
        val hashMap = HashMap<String, Any>()
        hashMap["titulo"] = "$titulo"
        hashMap["descripcion"]= "$descripcion"
        hashMap["categoria"] = "$id_seleccionado"

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "La actualizacion fue exitosa ", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "La actualizacion fallo debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

    private  var id_seleccionado =""
    private var titulo_seleccionado = ""
    //cuadro de dialogo para elegir categoria
    private fun dialogoCategoria() {
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

                binding.TvCategoriaLibro.text = titulo_seleccionado
            }
            .show()
    }

    private fun cargarCategorias() {
        catTituloArrayList = ArrayList()
        catIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categoria")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
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
                TODO("Not yet implemented")
            }
        })
    }
}