package com.example.prealba.Cliente

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.prealba.Administrador.MisFunciones
import com.example.prealba.Administrador.ModeloPdfQS
import com.example.prealba.databinding.ItemLibroClienteQsBinding

class AdaptadorPdfClienteQS : RecyclerView.Adapter<AdaptadorPdfClienteQS.HolderPdfClienteQS>, Filterable{
    //parametro itemView
    private lateinit var binding: ItemLibroClienteQsBinding

    private lateinit var m_context  : Context
    public lateinit var pdfArrayList : ArrayList<ModeloPdfQS>
    private lateinit var filtroLibro : ArrayList<ModeloPdfQS>

    private  var  filtroCL : FiltrarPdfClienteQS?=null

    constructor()
    constructor(m_context: Context, pdfArrayList: ArrayList<ModeloPdfQS>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroLibro = pdfArrayList
    }

    inner class HolderPdfClienteQS (itemView: View) : RecyclerView.ViewHolder(itemView){
        val VisualizadorPDFQS = binding.VisualizadorPDFQS
        val progressBarQS = binding.progressBarQS
        val Txt_titulo_libro_itemQS = binding.TxtTituloLibroItemQS
        val Txt_Objetivo_libro_itemQS = binding.TxtObjetivoLibroItemQS
        val Txt_acciones_libro_itemQS = binding.TxtAccionesLibroItemQS
        val Txt_telefono_libro_itemQS = binding.TxtTelefonoLibroItemQS
        val Txt_categoria_libro_adminQS = binding.TxtCategoriaLibroAdminQS
        val Txt_tamanio_libro_adminQS = binding.TxtTamanioLibroAdminQS
        val Txt_fecha_libro_adminQS = binding.TxtFechaLibroAdminQS


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfClienteQS {
        binding = ItemLibroClienteQsBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfClienteQS(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfClienteQS, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val categoriaId = modelo.categoria
        val titulo = modelo.titulo
        val objetivo = modelo.objetivo
        val acciones = modelo.acciones
        val telefono = modelo.telefono
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo

        val formatoTiempo = MisFunciones.formatoTiempo(tiempo)

        holder.Txt_titulo_libro_itemQS.text = titulo
        holder.Txt_Objetivo_libro_itemQS.text = objetivo
        holder.Txt_acciones_libro_itemQS.text = acciones
        holder.Txt_telefono_libro_itemQS.text = telefono
        holder.Txt_fecha_libro_adminQS.text = formatoTiempo


        MisFunciones.CargarCategoriaQS(categoriaId, holder.Txt_categoria_libro_adminQS)
        MisFunciones.CargarPdfUrl(pdfUrl, titulo, holder.VisualizadorPDFQS, holder.progressBarQS, null)
        MisFunciones.CargarTamanioPdf(pdfUrl, titulo, holder.Txt_tamanio_libro_adminQS)


        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, DetalleLibro_ClienteQS::class.java)
            intent.putExtra("idLibro", pdfId)
            m_context.startActivity(intent)
        }

    }

    override fun getFilter(): Filter {
        if (filtroCL == null) {
            filtroCL = FiltrarPdfClienteQS(filtroLibro, this)
        }
        return filtroCL as FiltrarPdfClienteQS

    }
}