package com.example.prealba.Cliente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.Administrador.ModeloPdfQS
import com.example.prealba.databinding.ActivityListaPdfClienteQsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaPdfClienteQs : AppCompatActivity() {

    private lateinit var binding: ActivityListaPdfClienteQsBinding

    private var idCategoria = ""
    private var tituloCategoria = ""

    private lateinit var pdfArrayList : ArrayList<ModeloPdfQS>
    private lateinit var  adaptadorPdfClienteQS: AdaptadorPdfClienteQS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPdfClienteQsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        idCategoria = intent.getStringExtra("idCategoria")!!
        tituloCategoria = intent.getStringExtra("tituloCategoria")!!

        binding.TxtCategriaLibroQSCLPdf.text = tituloCategoria

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        cargarLibrosQSCliente()

        binding.EtBuscarLibroClienteQSCLPdf.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(libro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adaptadorPdfClienteQS.filter.filter(libro)
                }catch (e: Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun cargarLibrosQSCliente() {
        pdfArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("LibrosQS")
        ref.orderByChild("categoria").equalTo(idCategoria)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for(ds in snapshot.children){
                        val modelo = ds.getValue(ModeloPdfQS::class.java)
                        if ( modelo!=null){
                            pdfArrayList.add(modelo)
                        }
                    }
                    adaptadorPdfClienteQS = AdaptadorPdfClienteQS( this@ListaPdfClienteQs, pdfArrayList)
                    binding.RvLibrosCliente.adapter = adaptadorPdfClienteQS


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
