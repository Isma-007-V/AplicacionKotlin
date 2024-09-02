package com.example.prealba.Administrador
//librerias importadas
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.MainActivity
import com.example.prealba.databinding.ActivityRegistrarAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registrar_Admin : AppCompatActivity() {

    //variables para progressdialog y cpnstrucciion de actividad
    private lateinit var  binding : ActivityRegistrarAdminBinding
    //configuracion del progressdialog
    private lateinit var  firebaseAuth : FirebaseAuth
    private lateinit var  progressDialog : ProgressDialog
    //Infla la interfaz de usuario y la vincula a un objeto binding.
    //Inicializa la autenticación Firebase.
    //Crea y configura un cuadro de diálogo de progreso.
    //Configura acciones para los clics en botones y elementos de texto en la interfaz de usuario.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistrarAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        //contexto
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnRegistrarAdmin.setOnClickListener{
            ValidarInformacion()
        }
        /*binding.TxtTengoCuenta.setOnClickListener{
            //startActivity(Intent(this@Registrar_Admin, Login_admin::class.java))
        }*/


    }
    var nombres = ""
    var email = ""
    var password = ""
    var r_password = ""
    private fun ValidarInformacion() {
        nombres = binding.EtNombresAdmin.text.toString().trim()
        email = binding.EtEmailAdmin.text.toString().trim()
        password = binding.EtPasswordAdmin.text.toString().trim()
        r_password = binding.EtRPasswordAdmin.text.toString().trim()

    //procedemos a relaizar la validacion de lso datos.
        if(nombres.isEmpty()){
            binding.EtNombresAdmin.error = "Ingrese email"
        }
        else if(email.isEmpty()){
            binding.EtEmailAdmin.error = "Ingrese email"
            binding.EtEmailAdmin.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailAdmin.error = "Email no es valido"
            binding.EtEmailAdmin.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordAdmin.error = "Ingrese la contraseña"
            binding.EtPasswordAdmin.requestFocus()
        }
        else if (password.length <6){
            binding.EtPasswordAdmin.error = "La contraseña debe tener mas de 6 caracteres"
            binding.EtPasswordAdmin.requestFocus()
        }
        else if (r_password.isEmpty()){
            binding.EtRPasswordAdmin.error = "Repita la contraseña"
            binding.EtRPasswordAdmin.requestFocus()
        }
        else if (password != r_password){
            binding.EtRPasswordAdmin.error = "Las contraseñas no coinciden"
            binding.EtRPasswordAdmin.requestFocus()
        }
        else
        {
            CrearCuentaAdmin(email, password)
        }
        
    }
    //creamos funcion

    private fun CrearCuentaAdmin(email: String, password: String) {
        progressDialog.setMessage("creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                AgregarInfoBD()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "ha fallado la cuenta debido a ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

    }
        //Esta función, llamada AgregarInfoBD, realiza las siguientes acciones:
        //Configura un mensaje de progreso.
        //Obtiene el tiempo actual y el ID de usuario de Firebase.
        //Crea un HashMap con datos del administrador.
        //Accede a la base de datos de Firebase.
        //Agrega los datos del administrador a la base de datos.
        //Muestra un mensaje de éxito y redirige a una actividad principal en caso de éxito.
        //Muestra un mensaje de error en caso de falla en la operación.
    private fun AgregarInfoBD() {
        progressDialog.setMessage("Guardando informacion...")
        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val datos_admin : HashMap<String, Any?> = HashMap()
        datos_admin["uid"]=uid
        datos_admin["nombre"]= nombres
        datos_admin["email"]= email
        datos_admin["rol"]= "admin"
        datos_admin["tiempo_registro"]=tiempo
        datos_admin["imagen"]= ""

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datos_admin)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Cuenta creada", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()

            }
            .addOnFailureListener() { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la informacion debido a ${e.message}", Toast.LENGTH_SHORT)
                    .show()

            }

    }
}



















