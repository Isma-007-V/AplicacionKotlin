package com.example.prealba.Administrador

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.prealba.MainActivity
import com.example.prealba.databinding.ActivityAgregarCategoriaQsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class Agregar_categoria_qs : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarCategoriaQsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private val GALLERY_REQUEST_CODE = 123
    private var imagenUriQS: Uri? = null
    private var categoriaQS=""



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAgregarCategoriaQsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Solicitar permisos antes de acceder a la galería
        binding.FBCambiarImgQS.setOnClickListener {
            solicitarPermisosGaleria()
        }

        binding.AgregarCategoriaBDQS.setOnClickListener {
            ValidaDatosCategoriaQS()
        }
    }
    /*
    private fun solicitarPermisosGaleria() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Si ya se tienen los permisos, abrir la galería
            openGallery()
        } else {
            // Si no se tienen permisos, solicitarlos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_REQUEST_CODE

            )
        }
    }
    */
    private fun solicitarPermisosGaleria() {
        // Verificar si la versión es Android 13 (API nivel 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13 y versiones superiores, utilizar el permiso READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), GALLERY_REQUEST_CODE)
            }
        } else {
            // Para versiones anteriores a Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST_CODE)
            }
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun ValidaDatosCategoriaQS() {
        categoriaQS = binding.EtCategoriaQS.text.toString().trim()
        if (categoriaQS.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese una categoría", Toast.LENGTH_SHORT).show()
        } else {
            AgregarQSCategoriaBD()
        }
    }

    private fun AgregarQSCategoriaBD() {
        progressDialog.setMessage("Agregando categoría")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        // Subir la imagen a Firebase Storage
        val storageRef = FirebaseStorage.getInstance().getReference("imagenes_categoria_qs/$tiempo.jpg")

        if (imagenUriQS != null) {
            storageRef.putFile(imagenUriQS!!)
                .addOnSuccessListener { taskSnapshot ->
                    // La imagen se subió exitosamente, ahora obtenemos la URL de descarga
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Ahora tenemos la URL de la imagen
                        val imageUrl = uri.toString()

                        // Crear el HashMap para la categoría con la URL de la imagen
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = "$tiempo"
                        hashMap["categoria"] = categoriaQS
                        hashMap["tiempo"] = tiempo
                        hashMap["uid"] = "${firebaseAuth.uid}"
                        hashMap["imageUrl"] = imageUrl

                        // Mostrar la imagen en el ImageView
                       // binding.imgCategoriaQS.setImageURI(imagenUriQS)

                        // Guardar en la base de datos
                        val ref = FirebaseDatabase.getInstance().getReference("CategoriaQS")
                        ref.child("$tiempo")
                            .setValue(hashMap)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    applicationContext,
                                    "Se agregó la categoría a la BD",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.EtCategoriaQS.setText("")
                                startActivity(Intent(this@Agregar_categoria_qs, MainActivity::class.java))
                                finishAffinity()
                            }
                            .addOnFailureListener { e ->
                                progressDialog.dismiss()
                                Toast.makeText(
                                    applicationContext,
                                    "No se agregó la categoría a la BD debido a ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Error al subir la imagen: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // Si no hay imagen, guardar sin la URL de la imagen
            val hashMap = HashMap<String, Any>()
            hashMap["id"] = "$tiempo"
            hashMap["categoria"] = categoriaQS
            hashMap["tiempo"] = tiempo
            hashMap["uid"] = "${firebaseAuth.uid}"

            val ref = FirebaseDatabase.getInstance().getReference("CategoriaQS")
            ref.child("$tiempo")
                .setValue(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Se agregó la categoría a la BD",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.EtCategoriaQS.setText("")
                    startActivity(Intent(this@Agregar_categoria_qs, MainActivity::class.java))
                    finishAffinity()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "No se agregó la categoría a la BD debido a ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Obtén la Uri de la imagen seleccionada
            imagenUriQS = data?.data

            // Muestra la imagen en el ImageView
            binding.imgCategoriaQS.setImageURI(imagenUriQS)

        }
    }

    // Manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, abrir la galería
                openGallery()
            } else {
                // Permiso denegado, mostrar un mensaje o realizar alguna acción adicional si es necesario
                Toast.makeText(
                    applicationContext,
                    "Permiso para acceder a la galería denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
