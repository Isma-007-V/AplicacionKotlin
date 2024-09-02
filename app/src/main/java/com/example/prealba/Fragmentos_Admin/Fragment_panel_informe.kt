
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.prealba.Administrador.AdaptadorListaUsuarios
import com.example.prealba.Modelos.ModeloDatosUsuario
import com.example.prealba.databinding.FragmentPanelInformeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Fragment_panel_informe : Fragment() {
    private lateinit var binding: FragmentPanelInformeBinding
    private lateinit var mContext: Context
    private lateinit var usersArrayList : ArrayList<ModeloDatosUsuario>
    private lateinit var adaptadorUsuarios : AdaptadorListaUsuarios

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPanelInformeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarUsuarios()
        binding.BuscarUsuario.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(name: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adaptadorUsuarios.filter.filter(name)
                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })



    }

    /*private fun listarUsuarios() {
        usersArrayList= ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("nombre")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList.clear()
                for(ds in snapshot.children){
                    val modelo = ds.getValue(ModeloDatosUsuario::class.java)
                    usersArrayList.add(modelo!!)

                }
                adaptadorUsuarios = AdaptadorListaUsuarios(mContext, usersArrayList)
                binding.categoriasRvUsuarios.adapter = adaptadorUsuarios
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }*/
    private fun listarUsuarios() {
        usersArrayList = ArrayList()
        // Referencia a la base de datos de Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        // ruta y la clave que voy ordenando/filtrando coincidan
        ref.orderByChild("rol").equalTo("cliente")
            .addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList.clear()
                for (ds in snapshot.children) {
                    // Aquí asumimos el modelo de datos se llama ModeloDatosUsuario y que es compatible con los datos de Firebase
                    val modelo = ds.getValue(ModeloDatosUsuario::class.java)
                    // Añade solo si el modelo no es nulo y tiene el rol "cliente"
                    modelo?.let {
                        if (it.rol == "cliente") {
                            usersArrayList.add(it)
                        }
                    }
                }
                adaptadorUsuarios = AdaptadorListaUsuarios(mContext, usersArrayList)
                binding.categoriasRvUsuarios.adapter = adaptadorUsuarios
            }

            override fun onCancelled(error: DatabaseError) {
                //  manejar el error de alguna manera.
                Toast.makeText(mContext, "Error al cargar los datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}
