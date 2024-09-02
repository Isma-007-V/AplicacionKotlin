package com.example.prealba.Cliente

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
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
import com.example.prealba.databinding.ActivityEditarPerfilClienteBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Editar_Perfil_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityEditarPerfilClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog : ProgressDialog

    private var imagenUri : Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

         progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnActualizarInfo.setOnClickListener{
            validarInformacion()
        }
        binding.FABCambiarImg.setOnClickListener{
            mostrarOpciones()
        }
    }
    //inicializacion de variables
    private var nombres = ""
    private var edad = ""
    private var sexo = ""
    private var localidad = ""
    private var telefonoC =""
    private var NSS = ""
    private var email = ""
    //evaluacion de variables
    private fun validarInformacion() {
        nombres = binding.EtCNombres.text.toString().trim()
        edad = binding.EtCEdad.text.toString().trim()
        sexo = binding.EtCSexo.text.toString().trim()
        localidad = binding.EtCLocalidad.text.toString().trim()
        telefonoC = binding.EtCTel.text.toString().trim()
        NSS =binding.EtCNSS.text.toString().trim()
        email = binding.EtCCorreo.text.toString().trim()

        if(nombres.isEmpty()){
            Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show()

        }
        else if (edad.isEmpty()){
            Toast.makeText(this, "Ingrese la edad", Toast.LENGTH_SHORT).show()
        }
        else if (sexo.isEmpty()){
            binding.TILSexo.error = "Ingrese el sexo"
            binding.TILSexo.requestFocus()
        }
        else if (localidad.isEmpty()){
            binding.TILLocalidad.error = "Ingrese localidad"
            binding.TILLocalidad.requestFocus()
        }
        else if (telefonoC.isEmpty()){
            binding.TILNumeroT.error = "Ingrese telefono"
            binding.TILNumeroT.requestFocus()
        }
        else if (NSS.isEmpty()){
            binding.TILNSS.error = "Ingrese el NSS"
            binding.TILNSS.requestFocus()
        }
        else if(email.isEmpty()){
            binding.TILCorreo.error = "Ingrese el correo"
            binding.TILCorreo.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.TILCorreo.error = "Correo no valido"
        }
        else{
            actualizarInformacion()
        }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizando informacion")
        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["nombre"] ="$nombres"
        hashMap["edad"] = "$edad"
        hashMap["sexo"] = "$sexo"
        hashMap["localidad"] = "$localidad"
        hashMap["nss"] = "$NSS"
        hashMap["telefono"] = "$telefonoC"
        hashMap["email"] = "$email"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Se actualizo la informacion", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "No se actualizo la informacion debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   val nombre = "${snapshot.child("nombre").value}"
                    val edad = "${snapshot.child("edad").value}"
                    val sexo = "${snapshot.child("sexo").value}"
                    val localidad = "${snapshot.child("localidad").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val correo = "${snapshot.child("email").value}"
                    val nss = "${snapshot.child("nss").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    binding.EtCNombres.setText(nombre)
                    binding.EtCEdad.setText(edad)
                    binding.EtCSexo.setText(sexo)
                    binding.EtCNSS.setText(nss)
                    binding.EtCTel.setText(telefono)
                    binding.EtCLocalidad.setText(localidad)
                    binding.EtCCorreo.setText(correo)
                    try{
                        Glide.with(this@Editar_Perfil_Cliente)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilCliente)
                    }catch (e:Exception){
                        Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this, binding.imgPerfilCliente)
        popupMenu.menu.add(Menu.NONE, 0,0, "Galeria")
        //modificamos la posicion o algo por el estilo
        popupMenu.menu.add(Menu.NONE, 1,1, "Cámara")
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
    //se llama cuando el permiso de la cámara ha sido concedido.
    private fun tomarFotografia(){
        //se crea un objeto ContentValues y se establece un título y una descripción para la imagen temporal que se va a tomar.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo_temp")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_temp")
        //contentResolver.insert para obtener una URI para la imagen temporal y se guarda en imagenUri.
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //Luego se crea un intento que se utiliza para abrir la cámara utilizando ACTION_IMAGE_CAPTURE y se pasa la jk
        // URI de la imagen temporal como un extra del intento a través de EXTRA_OUTPUT
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        ARLCamara.launch(intent)
    }
    //luego esto, como paso 3
    //se utiliza ARLCamara.launch() para iniciar la actividad de la cámara y se comprueba si el resultado de la actividad fue exitoso o no.
    private val ARLCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado->
            //Si fue exitoso, se llama a la función subirImageStorage() para subir la imagen a Firebase Storage, de lo
            // contrario, muestra un mensaje de cancelación.
            if(resultado.resultCode == RESULT_OK){
                subirImageStorage()
            }
            else{
                Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )
    //permisos para usar la cámara y la galería de fotos
    private val permisoCamara =
        //se utiliza para crear dos variables permisoCamara y permisoGaleria
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ Permiso_concedido->
            //solicitar permisos de cámara o galería de fotos utilizando el contrato ActivityResultContracts
            //i el permiso es concedido, se llama a las funciones tomarFotografia() o seleccionarImgGaleria()
            if(Permiso_concedido){
                tomarFotografia()
            }else{
                //Si el permiso es denegado o se presenta algún error, se muestra un mensaje de error correspondiente utilizando
                // la función Toast.makeText.
                Toast.makeText(applicationContext, "Permiso denegado",
                    Toast.LENGTH_SHORT).show()

            }
        }

    private val permisoGaleria =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ Permiso_concedido->
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

    private val ARLGaeleria = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado->
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
    private fun subirImageStorage() {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val rutaImagen = "RutaPerfilAdministradores/"+firebaseAuth.uid

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imagenUri!!)
            .addOnSuccessListener { taskSnapshot->
                val uriTask : Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagen = "${uriTask.result}"
                subirImagenDatabase(urlImagen)

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
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
}