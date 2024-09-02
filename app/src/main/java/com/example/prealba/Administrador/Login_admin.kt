package com.example.prealba.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prealba.MainActivity
import com.example.prealba.databinding.ActivityLoginAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login_admin : AppCompatActivity() {
    private  lateinit var binding: ActivityLoginAdminBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnLoginAdmin.setOnClickListener{
            ValidarInformacion()
        }
    }

    private var email = ""
    private var password=""

    private fun ValidarInformacion() {
        email = binding.EtEmailAdmin.text.toString().trim()
        password = binding.EtPasswordAdmin.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailAdmin.error = "Ingrese Correo "
            binding.EtEmailAdmin.requestFocus()
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailAdmin.error = "Correo invalido"
            binding.EtEmailAdmin.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordAdmin.error = "Ingrese una contraseña"
            binding.EtPasswordAdmin.requestFocus()
        }
        else{
            LoginAdmin()
        }

    }
    /*
    private fun LoginAdmin() {
        //progressDialog.setMessage("Iniciando sesión"): Configura un mensaje de progreso que dice "Iniciando sesión".
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()
        //firebaseAuth.signInWithEmailAndPassword(email, password): Utiliza la instancia de FirebaseAuth (firebaseAuth)
        // para intentar iniciar sesión con el correo electrónico (email) y la contraseña (password) proporcionados.
        firebaseAuth.signInWithEmailAndPassword(email, password)

            //.addOnSuccessListener  {}: Define una acción que se ejecutará si el inicio de sesión tiene éxito. En este caso, se oculta el cuadro
            // de diálogo de progreso y se inicia una nueva actividad (MainActivity) y se finaliza la actividad actual (Login_admin).
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_admin, MainActivity::class.java))
                finishAffinity()
            }
            //.addOnFailureListener { e -> }: Define una acción que se ejecutará si el inicio de sesión falla. En este caso, se oculta
            // el cuadro de diálogo de progreso y se muestra un mensaje de error en un
            // Toast que indica la razón de la falla, utilizando el mensaje proporcionado por la excepción e.
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo iniciar sesion debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

    }*/

    private fun LoginAdmin() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Aquí, el inicio de sesión fue exitoso. Ahora, verifica el rol del usuario.
                verificaRolDelUsuario()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo iniciar sesión debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun verificaRolDelUsuario() {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            // Referencia a la Realtime Database
            val dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")

            dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    // Obtén el valor del rol desde la Realtime Database
                    val rol = dataSnapshot.child("rol").getValue(String::class.java)
                    if (rol == "admin") {
                        // El usuario es un administrador, permite acceso a la actividad de administración.
                        startActivity(Intent(this@Login_admin, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        // No es un administrador, muestra un mensaje de error.
                        Toast.makeText(this@Login_admin, "Acceso denegado. No tienes permisos de administrador.", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut() // Opcional: desloguea al usuario si no es administrador.
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    progressDialog.dismiss()
                    Toast.makeText(this@Login_admin, "Error al verificar el rol del usuario: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            progressDialog.dismiss()
            Toast.makeText(this@Login_admin, "Error: Usuario no encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

}