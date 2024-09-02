package com.example.prealba.Cliente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.Modelos.Modelopdf
import com.example.prealba.databinding.ActivityTopVistasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopVistas : AppCompatActivity() {

    private lateinit var binding: ActivityTopVistasBinding
    private lateinit var pdfArrayList : ArrayList<Modelopdf>
    private lateinit var adaptadorPdfCliente : AdaptadorPdfCliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopVistasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topVistos()
    }

    private fun topVistos() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("contadorVistas").limitToLast(10)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    //recorrido a traves de la base de datos
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(Modelopdf::class.java)
                        pdfArrayList.add(modelo!!)
                    }
                    adaptadorPdfCliente = AdaptadorPdfCliente(this@TopVistas, pdfArrayList)
                    binding.RvTopVistos.adapter= adaptadorPdfCliente
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}