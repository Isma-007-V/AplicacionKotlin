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
import com.example.prealba.Modelos.Modelopdf
import com.example.prealba.databinding.ItemLibroClienteBinding

class AdaptadorPdfCliente : RecyclerView.Adapter<AdaptadorPdfCliente.HolderPdfCliente>, Filterable{
    //parametro itemView
    private lateinit var binding: ItemLibroClienteBinding

    private lateinit var m_context  : Context
    public lateinit var pdfArrayList : ArrayList<Modelopdf>

    private lateinit var filtroLibro : ArrayList<Modelopdf>
    private  var  filtro : FiltrarPdfCliente?=null

    constructor()
    constructor(m_context: Context, pdfArrayList: ArrayList<Modelopdf>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroLibro = pdfArrayList
    }

    inner class HolderPdfCliente (itemView: View) : RecyclerView.ViewHolder(itemView){
        val VisualizadorPDF = binding.VisualizadorPDF
        val progressBar = binding.progressBar
        val Txt_titulo_libro_item = binding.TxtTituloLibroItem
        val Txt_descripcion_libro_item = binding.TxtDescripcionLibroItem
        val Txt_categoria_libro_admin = binding.TxtCategoriaLibroAdmin
        val Txt_tamanio_libro_admin = binding.TxtTamanioLibroAdmin
        val Txt_fecha_libro_admin = binding.TxtFechaLibroAdmin



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfCliente {
        binding = ItemLibroClienteBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfCliente(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfCliente, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val categoriaId = modelo.categoria
        val titulo = modelo.titulo
        val descripcion = modelo.descripcion
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo

        val formatoTiempo = MisFunciones.formatoTiempo(tiempo)
        holder.Txt_titulo_libro_item.text = titulo
        holder.Txt_descripcion_libro_item.text = descripcion
        holder.Txt_fecha_libro_admin.text = formatoTiempo

        MisFunciones.CargarCategoria(categoriaId, holder.Txt_categoria_libro_admin)
        MisFunciones.CargarPdfUrl(pdfUrl, titulo, holder.VisualizadorPDF, holder.progressBar, null)
        MisFunciones.CargarTamanioPdf(pdfUrl, titulo, holder.Txt_tamanio_libro_admin)



        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, DetalleLibro_Cliente::class.java)
            intent.putExtra("idLibro", pdfId)
            m_context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        if(filtro == null){
            filtro = FiltrarPdfCliente(filtroLibro, this)

        }
        return filtro as FiltrarPdfCliente
    }
}