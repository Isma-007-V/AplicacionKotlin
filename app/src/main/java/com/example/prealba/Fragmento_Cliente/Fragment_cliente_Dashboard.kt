package com.example.prealba.Fragmento_Cliente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.prealba.Modelos.ModeloCategoria
import com.example.prealba.Cliente.AdaptadorCategoria_Cliente
import com.example.prealba.Cliente.TopDescargados
import com.example.prealba.Cliente.TopVistas
import com.example.prealba.databinding.FragmentClienteDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_cliente_Dashboard : Fragment() {

    private lateinit var binding : FragmentClienteDashboardBinding
    private lateinit var mContext : Context

    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>
    //pendiente ese ultimo detalle
    private lateinit var adaptadorCategoria : AdaptadorCategoria_Cliente

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentClienteDashboardBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarCategorias()

        binding.BuscarCategoria.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(categoria: CharSequence?, start: Int, before: Int, count: Int) {
                try{
                    adaptadorCategoria.filter.filter(categoria)
                }catch (e:Exception){
                    Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.BtnMasVistos.setOnClickListener{
            startActivity(Intent(mContext, TopVistas::class.java))
        }
        binding.BtnMasDescargados.setOnClickListener{
            startActivity(Intent(mContext, TopDescargados::class.java))
        }
    }

    private fun cargarCategorias(){
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categoria").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdaptadorCategoria_Cliente (mContext, categoriasArrayList)
                binding.categoriasRv.adapter = adaptadorCategoria

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


}