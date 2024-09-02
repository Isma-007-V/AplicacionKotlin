package com.example.prealba.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.MainActivityCliente
import com.example.prealba.R
import com.example.prealba.databinding.ActivityLoginClienteBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityLoginClienteBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog

    //inicio de sesion por medio de correo electronico de google
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnLoginCliente.setOnClickListener{
            validarInformacion()
        }
        /*binding.BtnLoginGoogle.setOnClickListener{
            iniciarSesionGoogle()
        }*/


    }

    private fun iniciarSesionGoogle() {
        val googleSignIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignIntent)
    }
    private  val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado->
        if (resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val cuenta = task.getResult(ApiException::class.java)
                autenticarGoogleFirebase(cuenta.idToken)
            }catch (e:Exception){
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()

        }
    }

    private fun autenticarGoogleFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                if(authResult.additionalUserInfo!!.isNewUser){
                    GuardarInformacionBD()
                }else{
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }

            }
            .addOnFailureListener{e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun GuardarInformacionBD() {
        //hasmap envia datos de user a firebase
        progressDialog.setMessage("Se esta subiendo informacion ")
        progressDialog.show()

        //obtener la informacion de la cuenta
        val uidGoogle = firebaseAuth.uid
        val emailGoogle = firebaseAuth.currentUser?.email
        val nombreGoogle = firebaseAuth.currentUser?.displayName

        //Convertir a string el nombre de usuario
        val nombre_usuario_google = nombreGoogle.toString()
        //obtenemos el tiempo
        val tiempo = System.currentTimeMillis()

        val datos_cliente = HashMap<String, Any?> ()

        datos_cliente["uid"] = uidGoogle
        datos_cliente["nombre"] = nombre_usuario_google
        datos_cliente["email"] = emailGoogle
        datos_cliente["edad"] = ""
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] = ""
        datos_cliente["rol"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivityCliente::class.java))
                Toast.makeText(applicationContext, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener{e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private  var email = ""
    private var password = ""

    private fun validarInformacion() {
        email = binding.EtEmailCl.text.toString().trim()
        password = binding.EtPasswordCl.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailCl.error = "Ingrese correo electronico"
            binding.EtEmailCl.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCl.error = "Correo no valido"
            binding.EtEmailCl.requestFocus()
        }else {
            loginCliente()
        }

    }
        /*
    private fun loginCliente() {
        progressDialog.setMessage("Iniciando sesion")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_Cliente, MainActivityCliente::class.java))
                finishAffinity()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo iniciar sesion debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

         */
        private fun loginCliente() {
            progressDialog.setMessage("Iniciando sesion")
            progressDialog.show()

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // Una vez que el inicio de sesión es exitoso, verifica el rol del usuario.
                    verificaRolDelUsuarioCliente()
                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "No se pudo iniciar sesion debido a ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }

    private fun verificaRolDelUsuarioCliente() {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")
            dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val rol = dataSnapshot.child("rol").getValue(String::class.java)
                    if (rol == "cliente") {
                        // El usuario es un cliente, permite acceso a la actividad principal del cliente.
                        startActivity(Intent(this@Login_Cliente, MainActivityCliente::class.java))
                        finishAffinity()
                    } else {
                        // No es un cliente, muestra un mensaje de error.
                        Toast.makeText(this@Login_Cliente, "Acceso denegado. No tienes permisos de cliente.", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut() // Opcional: desloguea al usuario si no es cliente.
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    progressDialog.dismiss()
                    Toast.makeText(this@Login_Cliente, "Error al verificar el rol del usuario: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            progressDialog.dismiss()
            Toast.makeText(this@Login_Cliente, "Error: Usuario no encontrado.", Toast.LENGTH_SHORT).show()
        }

    }

}