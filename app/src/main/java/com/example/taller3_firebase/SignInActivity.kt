package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.SignInActivityBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: SignInActivityBinding
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()

        binding.createButton.setOnClickListener {
            if (validateCreationForm()) {
                signUpAndSaveUserInfo()
            } else {
                Toast.makeText(
                    this@SignInActivity,
                    "Form is not valid. Try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun validateCreationForm():Boolean{
        var validEmail: Boolean = true
        var validPassword: Boolean = true
        var validNames: Boolean = true
        var validId: Boolean = true
        var validLocationNumbers: Boolean = true
        var validForm: Boolean = true

        // Check email
        val emailString = binding.editTextTextEmailAddress2.text.toString()
        validEmail = validateEmail(emailString)

        // Check password
        val passwordString = binding.editTextTextPassword2.text.toString()
        validPassword = validatePassword(passwordString)

        // Check names
        validNames = validateNames()

        // Check id
        validId = validateID()

        // Check location numbers
        validLocationNumbers = validateLocationNumbers()

        if (!validPassword || !validEmail || !validNames || !validId || !validLocationNumbers){
            validForm = false
            return validForm
        }

        return validForm
    }

    fun validateEmail(email: String): Boolean{
        var validEmail: Boolean = true

        if (TextUtils.isEmpty(email)){
            binding.editTextTextEmailAddress2.error = "Required"
            validEmail = false
            Log.d("email", email)
        } else {

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.editTextTextEmailAddress2.error = "Invalid"
                Log.d("email", email)
                validEmail = false
            }
        }

        return validEmail
    }

    fun validatePassword(password: String): Boolean{

        var validPassword: Boolean = true

        if (TextUtils.isEmpty(password)){
            binding.editTextTextPassword2.error = "Required"
            validPassword = false
            return validPassword
        }

        return validPassword
    }

    fun validateNames():Boolean{
        var validFirstName: Boolean = true
        var validLastName: Boolean = true
        val stringFirstName = binding.editTextFirstName.text.toString()
        val stringLastName = binding.editTextLastName.text.toString()

        if (TextUtils.isEmpty(stringFirstName)){
            binding.editTextFirstName.error = "Required"
            validFirstName = false
        }


        if (TextUtils.isEmpty(stringLastName)){
            binding.editTextLastName.error = "Required"
            validLastName = false
        }

        if (!validLastName || !validFirstName){
            return false
        }

        return true
    }

    fun validateID():Boolean{
        var stringId = binding.editTextID.text.toString()

        if(TextUtils.isEmpty(stringId)){
            binding.editTextID.error = "Required"
            return false
        }
        return true
    }

    fun validateLocationNumbers():Boolean{
        var latitudeString = binding.editTextNumberDecimalLatitude.text.toString()
        var longitudeString = binding.editTextNumberDecimalLongitude.text.toString()

        if(TextUtils.isEmpty(latitudeString)){
            binding.editTextNumberDecimalLatitude.error = "Required"
            return false
        }

        if(TextUtils.isEmpty(longitudeString)){
            binding.editTextNumberDecimalLongitude.error = "Required"
            return false
        }
        return true
    }

    fun signUpAndSaveUserInfo() {
        val emailString = binding.editTextTextEmailAddress2.text.toString()
        val passwordString = binding.editTextTextPassword2.text.toString()

        auth.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(
                        this@SignInActivity,
                        "User ${user?.email} was successfully created.",
                        Toast.LENGTH_LONG
                    ).show()

                    saveAdditionalUserInfo(user)
                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun saveAdditionalUserInfo(user: FirebaseUser?) {
        user?.let {
            val userId = it.uid
            val userDocument = hashMapOf(
                "email" to binding.editTextTextEmailAddress2.text.toString(),
                "firstName" to binding.editTextFirstName.text.toString(),
                "lastName" to binding.editTextLastName.text.toString(),
                "id" to binding.editTextID.text.toString(),
                "latitude" to binding.editTextNumberDecimalLatitude.text.toString(),
                "longitude" to binding.editTextNumberDecimalLongitude.text.toString(),
                "state" to "No disponible"
            )

            firestore.collection("usuarios")
                .document(userId)
                .set(userDocument)
                .addOnSuccessListener {
                    Log.d("Firestore", "Document successfully written!")
                    val intent = Intent(baseContext, ImageUpload::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error writing document", e)
                }
        }
    }

}