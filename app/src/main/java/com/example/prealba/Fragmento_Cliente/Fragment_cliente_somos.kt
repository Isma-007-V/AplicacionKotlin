package com.example.prealba.Fragmento_Cliente

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prealba.Administrador.ModeloCategoriaQS
import com.example.prealba.Cliente.AdaptadorCategoria_ClienteQS
import com.example.prealba.databinding.FragmentClienteSomosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_cliente_somos : Fragment() {

    private lateinit var binding : FragmentClienteSomosBinding
    private lateinit var mContext: Context
    private lateinit var categoriaArrayList : ArrayList<ModeloCategoriaQS>
    private lateinit var adaptadorCategoria : AdaptadorCategoria_ClienteQS

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentClienteSomosBinding.inflate(layoutInflater, container, false)
        return binding.root


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        cargarCategoriasQSCL()
        binding.BuscarCategoriaQSCL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(categoria: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adaptadorCategoria.filter.filter(categoria)
                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun cargarCategoriasQSCL() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("CategoriaQS").orderByChild("imageUri")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoriaQS::class.java)
                    categoriaArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdaptadorCategoria_ClienteQS(mContext, categoriaArrayList)
                binding.categoriasRvCQS.adapter = adaptadorCategoria
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}