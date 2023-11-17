package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ModifyInfoBinding
import com.example.taller3_firebase.databinding.SignInActivityBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ModifyInfo : AppCompatActivity() {

    lateinit var binding: ModifyInfoBinding
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModifyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val currentUser = auth.currentUser

        binding.createButton.setOnClickListener{
            if (validateCreationForm()){
                updateUserInfo()
                val intent = Intent(baseContext, MenuActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@ModifyInfo, String.format("Form is not valid. Try again."), Toast.LENGTH_SHORT).show()
            }
        }

        if (currentUser != null) {
            databaseReference.orderByChild("email").equalTo(currentUser.email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapshot in snapshot.children) {
                            val userEmail = userSnapshot.child("email").getValue(String::class.java)
                            val userFirstName = userSnapshot.child("firstName").getValue(String::class.java)
                            val userLastName = userSnapshot.child("lastName").getValue(String::class.java)
                            val userId = userSnapshot.child("id").getValue(String::class.java)
                            val userLatitude = userSnapshot.child("latitude").getValue(String::class.java)
                            val userLongitude = userSnapshot.child("longitude").getValue(String::class.java)

                            binding.editTextTextEmailAddress2.setText(userEmail)
                            binding.editTextFirstName.setText(userFirstName)
                            binding.editTextLastName.setText(userLastName)
                            binding.editTextID.setText(userId)
                            binding.editTextNumberDecimalLatitude.setText(userLatitude)
                            binding.editTextNumberDecimalLongitude.setText(userLongitude)

                            // Disable the ID field
                            binding.editTextID.isEnabled = false
                            binding.editTextTextPassword2.isEnabled = false
                            binding.editTextTextEmailAddress2.isEnabled = false
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })


        }


    }

    fun validateCreationForm():Boolean{
        var validEmail: Boolean = true

        var validNames: Boolean = true
        var validId: Boolean = true
        var validLocationNumbers: Boolean = true
        var validForm: Boolean = true

        // Check email
        val emailString = binding.editTextTextEmailAddress2.text.toString()
        validEmail = validateEmail(emailString)

        // Check names
        validNames = validateNames()

        // Check id
        validId = validateID()

        // Check location numbers
        validLocationNumbers = validateLocationNumbers()

        if (!validEmail || !validNames || !validId || !validLocationNumbers){
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


    fun updateUserInfo(){

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