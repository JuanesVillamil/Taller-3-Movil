package com.example.taller3_firebase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.taller3_firebase.databinding.ImageUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger

class ImageUpload : AppCompatActivity(){
    private lateinit var binding: ImageUploadBinding

    companion object {
        val TAG: String = ImageUpload::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    // Permission handler
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }


    var pictureImagePath: Uri? = null
    var imageViewContainer: ImageView? = null

    // Create ActivityResultLauncher instances
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle camera result
            imageViewContainer!!.setImageURI(pictureImagePath)
            imageViewContainer!!.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            logger.info("Image capture successfully.")
        } else {
            logger.warning("Image capture failed.")
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle gallery result
            val imageUri: Uri? = result.data!!.data
            imageViewContainer!!.setImageURI(imageUri)
            imageViewContainer!!.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            logger.info("Image loaded successfully")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageViewContainer = binding.fotoPerfilPersona

        binding.cambioFotoCamaraButton.setOnClickListener {
            verifyPermissions(this, android.Manifest.permission.CAMERA, "El permiso es requerido para...")
        }

        binding.cambioFotoGaleriaButton.setOnClickListener{
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }

        binding.finishBtn.setOnClickListener{
            val intent = Intent(baseContext, MenuActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            // Handle camera result
            imageViewContainer!!.setImageURI(pictureImagePath)
            imageViewContainer!!.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            logger.info("Image capture successfully.")
            val selectedImageUri: Uri = data?.data!!
            uploadPhoto(selectedImageUri)
        } else {
            logger.warning("Image capture failed.")
        }
    }

    // Verify permission to access contacts info
    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // We display a snackbar with the justification for the permission, and once it disappears, we request it again.
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }

    // Update activity behavior and actions according to result of permission request
    fun updateUI(permission : Boolean) {
        if(permission){
            //granted
            logger.info("Permission granted")
            dipatchTakePictureIntent()
        }else{
            logger.warning("Permission denied")
        }
    }

    fun dipatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Crear el archivo donde debería ir la foto
        var imageFile: File? = null
        pictureImagePath = null
        imageViewContainer!!.setImageURI(null)
        try {
            imageFile = createImageFile()
        } catch (ex: IOException) {
            logger.warning(ex.message)
        }
        // Continua si el archivo ha sido creado exitosamente
        if (imageFile != null) {
            // Guardar un archivo: Ruta para usar con ACTION_VIEW intents
            pictureImagePath = FileProvider
                .getUriForFile(this,"com.example.taller3_firebase.fileprovider", imageFile)
            logger.info("Ruta: ${pictureImagePath}")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)
            try {
                cameraActivityResultLauncher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                logger.warning("Camera app not found.")
            }
        }
    }


    fun uploadPhoto(imageUri: Uri){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val storageReference =
                FirebaseStorage.getInstance().getReference("profile_images/$it.jpg")

            storageReference.putFile(imageUri)
                .addOnSuccessListener {
                    // Image uploaded successfully

                    val imageUrl = it.metadata?.reference?.downloadUrl.toString()

                    // TODO: Update UI or perform other actions after successful image upload
                }
                .addOnFailureListener {
                    // Handle unsuccessful image upload
                    // Display an error message to the user
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        //Crear un nombre de archivo de imagen
        val timeStamp: String = DateFormat.getDateInstance().format(Date())
        val imageFileName = "${timeStamp}.jpg"
        val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)
        return imageFile
    }

}