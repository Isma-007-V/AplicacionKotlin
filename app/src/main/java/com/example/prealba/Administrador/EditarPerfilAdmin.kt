package com.example.prealba.Administrador

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.prealba.R
import com.example.prealba.databinding.ActivityEditarPerfilAdminBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditarPerfilAdmin : AppCompatActivity() {
    private  lateinit var binding : ActivityEditarPerfilAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var imagenUri : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere uno poco por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.FbCambiarImg.setOnClickListener{
            mostrarOpciones()
        }

        binding.BtnActualizarInfo.setOnClickListener{
            validarInformacion()
        }

    }

    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this, binding.imgPerfilAdmin)
        popupMenu.menu.add(Menu.NONE, 0,0, "Galeria")
        //modificamos la posicion o algo por el estilo
        //popupMenu.menu.add(Menu.NONE, 1,1, "Cámara")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val id = item.itemId
            if(id==0){
                //Elegir una imagen de la galeria
                if(ContextCompat.checkSelfPermission(applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    seleccionarImgGaleria()
                }else{
                    permisoGaleria.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else if (id == 1){
                //tomar una fotografia
                if (ContextCompat.checkSelfPermission(applicationContext,
                    android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                    tomarFotografia()
                }else
                    permisoCamara.launch(android.Manifest.permission.CAMERA)
            }
            true
        }

    }




    //esto es lo que sigue como paso 2
    private fun tomarFotografia() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo_temp")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_temp")
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        ARLCamara.launch(intent)
    }
    //luego esto, como paso 3
    private val ARLCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {resultado->
            if(resultado.resultCode == Activity.RESULT_OK){
                subirImageStorage()
            }
            else{
                Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )
    private val permisoCamara =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso_concedido->
            if(Permiso_concedido){
                tomarFotografia()
            }else{
                Toast.makeText(applicationContext, "Permiso denegado",
                    Toast.LENGTH_SHORT).show()

            }
        }

    private val permisoGaleria =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso_concedido->
            if(Permiso_concedido){
                seleccionarImgGaleria()
            }
            else{
                Toast.makeText(applicationContext, "Se presento algun error ", Toast.LENGTH_SHORT).show()
            }

        }

    private fun seleccionarImgGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        //tipo de archivo
        intent.type= "image/*"
        ARLGaeleria.launch(intent)
    }

    private val ARLGaeleria = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data

                //binding.imgPerfilAdmin.setImageURI(imagenUri)
                subirImageStorage()
            }else{
                Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
            }

        }
    )

    private var nombres = ""
    private fun validarInformacion() {
        nombres = binding.EtANombres.text.toString().trim()
        if(nombres.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese un nuevo nombre", Toast.LENGTH_SHORT).show()
        }else {
            ActualizarInfo()

        }
    }

    private fun subirImageStorage() {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        // Usar el tiempo actual como nombre de archivo
        val nombreArchivo = "RutaORGActual/" + System.currentTimeMillis()

        val ref = FirebaseStorage.getInstance().getReference(nombreArchivo)
        ref.putFile(imagenUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagen = uriTask.result.toString()
                subirImagenDatabase(urlImagen)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
    }


    private fun subirImagenDatabase(urlImagen : String) {
        progressDialog.setMessage("Actualizando imagen")

        val hashMap : HashMap<String, Any> = HashMap()
        if (imagenUri != null){
            hashMap["imagen"] = urlImagen
        }
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "su imagen se ha actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun ActualizarInfo() {
        progressDialog.setMessage("Actualizaando información")
        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["nombres"] = "${nombres}"
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "se actualizo la informacion", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo realizar la actualizacion debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Obtener la informacion de tiempo real
                    val nombre = "${snapshot.child("nombre").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    binding.EtANombres.setText(nombre)

                    try {
                        Glide.with(applicationContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilAdmin)

                    }catch (e:Exception){
                        Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}