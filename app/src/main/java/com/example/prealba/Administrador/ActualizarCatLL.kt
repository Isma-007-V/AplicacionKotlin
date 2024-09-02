package com.example.prealba.Administrador

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.databinding.ActivityActualizarCatLlBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarCatLL : AppCompatActivity() {

    private lateinit var  binding : ActivityActualizarCatLlBinding
    private var idcategoria = ""

    private lateinit var progressDialog : ProgressDialog
    //titulos
    private lateinit var catTLLArrayList: ArrayList<String>

    //id
    private lateinit var catIdLLAArrayList: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarCatLlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idcategoria = intent.getStringExtra("idCatLL") ?: return

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)


        //cargarCategorias()
        cargarInformacion()
        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.AgregarCategoriaBDQS2.setOnClickListener{
            validarinformacion()

        }

    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("OrganismoNumeros")
        ref.child(idcategoria)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener la informacion en tiempo real del libro seleccionado
                    val titulo = snapshot.child("categoria").value?.toString() ?: "Valor predeterminado"
                    val numeroOrg = snapshot.child("numeroTelefonico").value?.toString() ?: "Valor predeterminado"

                    //val imagen = "${snapshot.child("imageUrl").value}"

                    binding.EtCategoriaQSLL1.setText(titulo)
                    binding.EtNumeroQSLL2.setText(numeroOrg)

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var titulo = ""
    private var numeroOrg = ""
    private fun validarinformacion() {
        titulo = binding.EtCategoriaQSLL1.text.toString().trim()
        numeroOrg = binding.EtNumeroQSLL2.text.toString().trim()

        if(titulo.isEmpty()){
            Toast.makeText(this, "Ingrese titulo", Toast.LENGTH_SHORT).show()
        }
        else if (numeroOrg.isEmpty()){
            Toast.makeText(this, "Ingrese descripcion", Toast.LENGTH_SHORT).show()

        }else{
            actualizarInformacion()
        }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizando informacion")
        progressDialog.show()
        val hashMap = HashMap<String, Any>()
        hashMap["categoria"] = "$titulo"
        hashMap["numeroTelefonico"]= "$numeroOrg"
        //hashMap["categoria"] = "$id_seleccionado"

        val ref = FirebaseDatabase.getInstance().getReference("OrganismoNumeros")
        ref.child(idcategoria)
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


    /*private fun cargarCategorias() {
        catTLLArrayList = ArrayList()
        catIdLLAArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("OrganismoNumeros")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catTLLArrayList.clear()
                catIdLLAArrayList.clear()
                for(ds in snapshot.children){
                    val id= ""+ds.child("id")
                    val categoria= ""+ds.child("categoria").value

                    catTLLArrayList.add(categoria)
                    catIdLLAArrayList.add(id)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }*/
}