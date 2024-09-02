package com.example.prealba.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.MainActivityCliente
import com.example.prealba.databinding.ActivityRegistrarClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registrar_Cliente : AppCompatActivity() {
    private lateinit var binding : ActivityRegistrarClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistrarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnRegistrarCl.setOnClickListener{
            validarInformacion()
        }
        binding.TxtTengoCuenta.setOnClickListener{
            startActivity(Intent(this@Registrar_Cliente, Login_Cliente::class.java))
        }
    }
    var nombres = ""
    var sexo = ""
    var edad = ""
    var localidad = ""
    var telefonoC =""
    var NSS = ""
    var email = ""
    var password = ""
    var r_password= ""

    private fun validarInformacion() {
        nombres = binding.EtNombreCl.text.toString().trim()
        sexo = binding.EtSexoCl.text.toString().trim()
        edad =binding.EtEdadCl.text.toString().trim()
        localidad = binding.EtLocalidadCl.text.toString().trim()
        telefonoC = binding.EtTelefonoClR.text.toString().trim()
        NSS =binding.EtNSSCl.text.toString().trim()
        email = binding.EtEmailCl.text.toString().trim()
        //telefono = binding.EtnumeroTcl.text.toString().trim()
        //CURP = binding.EtCURPcl.text.toString().trim()
        //NSS = binding.EtNSScl.text.toString().trim()

        password = binding.EtPasswordCl.text.toString().toString()
        r_password = binding.EtRPasswordCl.text.toString().toString()

        //validaciones para los datos con resepecto a sus variables
        if (nombres.isEmpty()){
            binding.EtNombreCl.error = "Ingrese nombres"
            binding.EtNombreCl.requestFocus()
        }
        else if (sexo.isEmpty()){
            binding.EtSexoCl.error = "Ingrese el sexo"
            binding.EtSexoCl.requestFocus()
        }
        else if(edad.isEmpty()){
            binding.EtEdadCl.error = "Ingrese la edad"
            binding.EtEdadCl.requestFocus()
        }
        else if (localidad.isEmpty()){
            binding.EtLocalidadCl.error = "Ingrese localidad"
            binding.EtLocalidadCl.requestFocus()
        }
        else if (telefonoC.isEmpty()){
            binding.EtTelefonoClR.error = "Ingrese telefono"
            binding.EtTelefonoClR.requestFocus()
        }
        else if (NSS.isEmpty()){
            binding.EtNSSCl.error = "Ingrese el NSS"
            binding.EtNSSCl.requestFocus()
        }
        else if(email.isEmpty()){
            binding.EtEmailCl.error = "Ingrese el correo"
            binding.EtEmailCl.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCl.error = "Correo no valido"
        }
        else if (password.isEmpty()){
            binding.EtPasswordCl.error = "Ingrese una contraseña"
            binding.EtPasswordCl.requestFocus()
        }
        else if(password.length<6){
            binding.EtPasswordCl.requestFocus()
        }
        else if (r_password.isEmpty()){
            binding.EtRPasswordCl.error = "Confirme constraseña"
            binding.EtRPasswordCl.requestFocus()
        }
        else {
            crearCuentaCliente(email, password)
        }



    }

    private fun crearCuentaCliente(email: String, password: String) {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                agregarInfoDB()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Ha fallado el registro del cliente debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
    //registrar info en BD
    private fun agregarInfoDB() {
        progressDialog.setMessage("Guardando información...")

        val tiempo = System.currentTimeMillis()

        val uid = firebaseAuth.uid!!

        val datos_cliente : HashMap<String, Any> = HashMap()

        datos_cliente["uid"] = uid
        datos_cliente["nombre"] = nombres
        datos_cliente["sexo"] = sexo
        datos_cliente["edad"] = edad
        datos_cliente["localidad"] = localidad
        datos_cliente["nss"] = NSS
        datos_cliente["telefono"] = telefonoC
        datos_cliente["email"] = email
        datos_cliente["rol"] = "cliente"
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] =""

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La informacion se guardo correctamente",
                    Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
                startActivity(Intent(this@Registrar_Cliente, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Ha fallado el registro del cliente debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()

            }


    }
}