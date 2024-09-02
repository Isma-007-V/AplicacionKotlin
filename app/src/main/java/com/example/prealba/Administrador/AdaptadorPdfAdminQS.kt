package com.example.prealba.Administrador

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prealba.databinding.ItemLibroAdminQsBinding

class AdaptadorPdfAdminQS : RecyclerView.Adapter<AdaptadorPdfAdminQS.HolderPdfAdminQS>, Filterable{
    private lateinit var binding: ItemLibroAdminQsBinding
    private  var m_context  : Context
    public var pdfArrayList : ArrayList<ModeloPdfQS>
    private var filtroLibro : ArrayList<ModeloPdfQS>
    private  var  filtro : FiltroPdfAdminQS?=null


    constructor(m_context: Context, pdfArrayList: ArrayList<ModeloPdfQS>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroLibro = pdfArrayList
    }

    inner class HolderPdfAdminQS (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val VisualizadorPDFQS = binding.VisualizadorPDFQS
        val progressBarQS = binding.progressBarQS
        val Txt_titulo_libro_itemQS = binding.TxtTituloLibroItemQS
        val Txt_Objetivo_libro_itemQS = binding.TxtObjetivoLibroItemQS
        val Txt_acciones_libro_itemQS = binding.TxtAccionesLibroItemQS
        val Txt_telefono_libro_itemQS = binding.TxtTelefonoLibroItemQS
        val Txt_categoria_libro_adminQS = binding.TxtCategoriaLibroAdminQS
        val Txt_tamanio_libro_adminQS = binding.TxtTamanioLibroAdminQS
        val Txt_fecha_libro_adminQS = binding.TxtFechaLibroAdminQS
        val Ib_mas_opciones = binding.IbMasOpciones


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdminQS {
        binding = ItemLibroAdminQsBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfAdminQS(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }
    //Obtiene los datos del modelo de PDF en la posición actual de la lista.
    //Extrae información relevante del modelo, como el ID del PDF, la categoría, el título,objetivo, las acciones, el teléfono, la URL del PDF y
    // el tiempo.
    override fun onBindViewHolder(holder: HolderPdfAdminQS, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val categoriaId = modelo.categoria
        val titulo = modelo.titulo
        val objetivo = modelo.objetivo
        val acciones = modelo.acciones
        val telefono = modelo.telefono
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo
        //Actualiza varios elementos de la interfaz de usuario con la información obtenida, como el título, el objetivo,
        // las acciones, el teléfono, la fecha y la categoría del PDF.
        val formatoTiempo = MisFunciones.formatoTiempo(tiempo)

        holder.Txt_titulo_libro_itemQS.text = titulo
        holder.Txt_Objetivo_libro_itemQS.text = objetivo
        holder.Txt_acciones_libro_itemQS.text = acciones
        holder.Txt_telefono_libro_itemQS.text = telefono
        holder.Txt_fecha_libro_adminQS.text = formatoTiempo
        //Utiliza funciones personalizadas (como MisFunciones.CargarCategoriaQS, MisFunciones.CargarPdfUrl y MisFunciones.CargarTamanioPdf) para
        // cargar y mostrar información adicional, como la categoría del PDF, el PDF en sí y su tamaño.
        MisFunciones.CargarCategoriaQS(categoriaId, holder.Txt_categoria_libro_adminQS)
        MisFunciones.CargarPdfUrl(pdfUrl, titulo, holder.VisualizadorPDFQS, holder.progressBarQS, null)
        MisFunciones.CargarTamanioPdf(pdfUrl, titulo, holder.Txt_tamanio_libro_adminQS)

        holder.Ib_mas_opciones.setOnClickListener{
            verOpciones(modelo, holder)
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, Detalle_Libro_QS::class.java)
            intent.putExtra("idLibro", pdfId)
            m_context.startActivity(intent)
        }

    }

    private fun verOpciones(modelo: ModeloPdfQS, holder: AdaptadorPdfAdminQS.HolderPdfAdminQS) {
        val idLibro = modelo.id
        val urlLibro = modelo.url
        val tituloLibro = modelo.titulo
        val objetivo = modelo.objetivo
        val acciones = modelo.acciones
        val telefono = modelo.telefono

        val opciones = arrayOf("Actualizar", "Eliminar")

        var builder = AlertDialog.Builder(m_context)
        builder.setTitle("Seleccione una opción")
            //ActualizarLibroQS y se le pasa el id del libro como un extra del intent para poder actualizar la información del mismo.
            .setItems(opciones){ dialog, posicion->
                if(posicion == 0){
                    //actualizar
                    val intent = Intent(m_context, ActualizarLibroQS::class.java)
                    intent.putExtra("idLibro", idLibro)
                    m_context.startActivity(intent)
                }else if(posicion == 1){
                    //eliminar
                    //Si el usuario selecciona "Eliminar", un diálogo de confirmación aparece preguntando si están
                    // seguros de eliminar el libro.
                    val opcionesEliminacion = arrayOf("Si", "Cancelar")
                    val builder = AlertDialog.Builder(m_context)
                    builder.setTitle("¿Estás seguro de eliminar el libro ${tituloLibro}?")
                        //Si el usuario confirma, se llama a la función EliminarLibroQS para eliminar el libro de la base de datos y
                        // de Firebase Storage.
                        .setItems(opcionesEliminacion){ dialog, posicion->
                            if(posicion == 0){
                                MisFunciones.EliminarLibroQS(m_context, idLibro, urlLibro, tituloLibro)
                            }
                            //Si el usuario selecciona "Cancelar", se muestra un mensaje indicando que la operación fue cancelada.
                            else if (posicion == 1){
                                Toast.makeText(m_context, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .show()
                }
            }
            .show()
    }
    override fun getFilter(): Filter {
        if(filtro == null){
            filtro = FiltroPdfAdminQS(filtroLibro, this)
        }
        return filtro as FiltroPdfAdmin
        }
    }

