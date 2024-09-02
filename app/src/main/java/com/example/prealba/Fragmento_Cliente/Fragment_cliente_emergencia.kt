package com.example.prealba.Fragmento_Cliente

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prealba.Administrador.ModeloCategoriaLlamadasAdmin
import com.example.prealba.Cliente.AdaptadorCategoriaLlamadasCliente
import com.example.prealba.databinding.FragmentClienteEmergenciaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_cliente_emergencia : Fragment() {
    private lateinit var binding : FragmentClienteEmergenciaBinding
    private lateinit var mContext: Context
    private lateinit var categoriaArrayList : ArrayList<ModeloCategoriaLlamadasAdmin>
    private lateinit var adaptadorCategoria : AdaptadorCategoriaLlamadasCliente


    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentClienteEmergenciaBinding.inflate(layoutInflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ListarCategoriasLlamadasOrgCL()
        /*binding.BuscarCategoriaQS.addTextChangedListener(object : TextWatcher {
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
        })*/




    }

    private fun ListarCategoriasLlamadasOrgCL() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("OrganismoNumeros").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoriaLlamadasAdmin::class.java)

                    categoriaArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdaptadorCategoriaLlamadasCliente(mContext, categoriaArrayList)
                binding.categoriasRvQS.adapter = adaptadorCategoria

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

