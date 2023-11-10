package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize firebase auth
        auth = Firebase.auth

        binding.loginButton.setOnClickListener{
            val validForm = validateForm()

            if (validForm){
                val intent = Intent(baseContext, MenuActivity::class.java)
                startActivity(intent)
            }

            if (!validForm){
                Toast.makeText(this@MainActivity, String.format("Form is not valid. Try again."), Toast.LENGTH_SHORT).show()
            }
        }

        // Add event listener to sign up button
        binding.signupButton.setOnClickListener{
            val intent = Intent(baseContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()

        // Check if there is a user currenty logged in.
        val currentUser = auth.currentUser

        // If there is, launch the menu activity.
        if (currentUser != null){
            val intent = Intent(baseContext, MainActivity::class.java)
            intent.putExtra("currentUser", currentUser.email)
            startActivity(intent)
        }

        // If not, stay on the login activity.
        binding.editTextTextEmailAddress.setText("")
        binding.editTextTextPassword.setText("")

    }


    fun validateForm():Boolean{
        var validEmail: Boolean
        var validPassword: Boolean

        // Check email
        val emailString = binding.editTextTextEmailAddress.toString()
        // First check if its empty.
        if (TextUtils.isEmpty(emailString)){
            binding.editTextTextEmailAddress.error = "Required"
            validEmail = false
        } else { // Then check if its a valid pattern.
            validEmail = validateEmail(emailString)
        }



        // Check password
        val passwordString = binding.editTextTextPassword.toString()
        if (TextUtils.isEmpty(passwordString)){
            binding.editTextTextPassword.error = "Required"
            validPassword = false
        }

        return false
    }

    fun validateEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }


    }
     */


    /*
    private fun saveUserToDatabase(userId: String, firstName: String, lastName: String, idNumber: String, imageUrl: String, latitude: Double, longitude: Double) {
        val userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val newUser = User(firstName, lastName, idNumber, imageUrl, latitude, longitude)
        userReference.setValue(newUser)
    }

     */
}