package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.SignInActivityBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: SignInActivityBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createButton.setOnClickListener{
            if (validateCreationForm()){
                val intent = Intent(baseContext, MenuActivity::class.java)
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
        val emailString = binding.editTextTextEmailAddress2.toString()
        validEmail = validateEmail(emailString)

        // Check password
        val passwordString = binding.editTextTextPassword2.toString()
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
        } else { // Then check if its a valid pattern.

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.editTextTextEmailAddress2.error = "Invalid"
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
        val stringFirstName = binding.editTextFirstName.toString()
        val stringLastName = binding.editTextLastName.toString()

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
        var stringId = binding.editTextID.toString()

        if(TextUtils.isEmpty(stringId)){
            binding.editTextID.error = "Required"
            return false
        }
        return true
    }

    fun validateLocationNumbers():Boolean{
        var latitudeString = binding.editTextNumberDecimalLatitude.toString()
        var longitudeString = binding.editTextNumberDecimalLongitude.toString()

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


/*






        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        // TODO: Update UI or navigate to the next screen
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

 */
}