package com.example.prealba.Fragmentos_Admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prealba.Administrador.AdaptadorCategoriaLlamadasAdmin
import com.example.prealba.Administrador.AgregarCategoriaLlamadaAdmin
import com.example.prealba.Administrador.ModeloCategoriaLlamadasAdmin
import com.example.prealba.databinding.FragmentAdminEmergenciasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_admin_emergencias : Fragment() {

    private lateinit var binding : FragmentAdminEmergenciasBinding
    private lateinit var mContext: Context
    private lateinit var categoriaArrayList : ArrayList<ModeloCategoriaLlamadasAdmin>
    private lateinit var adaptadorCategoria : AdaptadorCategoriaLlamadasAdmin

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAdminEmergenciasBinding.inflate(layoutInflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ListarCategoriasLlamadasOrg()
        binding.BuscarCategoriaQS.addTextChangedListener(object : TextWatcher {
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


        binding.BtnAgregarCategoriaQS.setOnClickListener{
            startActivity(Intent(mContext, AgregarCategoriaLlamadaAdmin::class.java))
        }



    }

    private fun ListarCategoriasLlamadasOrg() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("OrganismoNumeros").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoriaLlamadasAdmin::class.java)

                    categoriaArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdaptadorCategoriaLlamadasAdmin(mContext, categoriaArrayList)
                binding.categoriasRvQS.adapter = adaptadorCategoria

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    }

