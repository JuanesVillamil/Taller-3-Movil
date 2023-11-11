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
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlin.math.log

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: SignInActivityBinding
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.createButton.setOnClickListener{
            if (validateCreationForm()){
                signUpAndSaveUserInfo()
                saveAdditionUserInfo()

                val intent = Intent(baseContext, ImageUpload::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@SignInActivity, String.format("Form is not valid. Try again."), Toast.LENGTH_SHORT).show()
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

        // First check if its empty.
        if (TextUtils.isEmpty(email)){
            binding.editTextTextEmailAddress2.error = "Required"
            validEmail = false
            Log.d("email", email)
        } else { // Then check if its a valid pattern.

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

                    // Now that the user is created, save additional user info

                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun saveAdditionUserInfo(){

        val database = Firebase.database
        val userDefinedId = (binding.editTextID.text.toString())
        val reference: DatabaseReference = database.getReference("users/$userDefinedId")
        reference.child("email").setValue(binding.editTextTextEmailAddress2.text.toString())
        reference.child("firstName").setValue(binding.editTextFirstName.text.toString())
        reference.child("firstName").setValue(binding.editTextFirstName.text.toString())
        reference.child("lastName").setValue(binding.editTextLastName.text.toString())
        reference.child("id").setValue(binding.editTextID.text.toString())
        reference.child("latitude").setValue(binding.editTextNumberDecimalLatitude.text.toString())
        reference.child("longitude").setValue(binding.editTextNumberDecimalLongitude.text.toString())
        reference.child("state").setValue("No disponible")

    }

}