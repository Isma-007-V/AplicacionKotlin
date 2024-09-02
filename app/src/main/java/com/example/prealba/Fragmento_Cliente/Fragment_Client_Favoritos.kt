package com.example.prealba.Fragmento_Cliente

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prealba.Modelos.Modelopdf
import com.example.prealba.Cliente.AdaptadorPdfFavorito
import com.example.prealba.databinding.FragmentClientFavoritosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Fragment_Client_Favoritos : Fragment() {
    private lateinit var binding : FragmentClientFavoritosBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var librosArrayList: ArrayList<Modelopdf>
    private lateinit var  adatadorPdfFav : AdaptadorPdfFavorito
    private lateinit var mContext : android.content.Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentClientFavoritosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarFavoritos()
    }
    private fun cargarFavoritos(){
        librosArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    librosArrayList.clear()
                    for (ds in snapshot.children){
                        val idLibro = "${ds.child("id").value}"
                        val modelopdf = Modelopdf()
                        modelopdf.id = idLibro

                        librosArrayList.add(modelopdf)
                    }
                    adatadorPdfFav = AdaptadorPdfFavorito(mContext, librosArrayList)
                    binding.RvLibrosFavoritos.adapter = adatadorPdfFav
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}