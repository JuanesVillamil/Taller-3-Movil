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

        auth = Firebase.auth

        binding.loginButton.setOnClickListener{
            val validForm = validateLoginForm()

            if (validForm){
                val intent = Intent(baseContext, MenuActivity::class.java)
                startActivity(intent)
            }

            if (!validForm){
                Toast.makeText(this@MainActivity, String.format("Form is not valid. Try again."), Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupButton.setOnClickListener{
            val intent = Intent(baseContext, SignInActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(baseContext, MenuActivity::class.java)
            intent.putExtra("currentUser", currentUser.email)
            startActivity(intent)
        }

        binding.editTextTextEmailAddress.setText("")
        binding.editTextTextPassword.setText("")

        binding.loginButton.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(baseContext, MenuActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


        }
    }


    fun validateLoginForm():Boolean{
        var validEmail: Boolean = true
        var validPassword: Boolean = true
        var validForm: Boolean = true

        // Check email
        val emailString = binding.editTextTextEmailAddress.text.toString()
        validEmail = validateEmail(emailString)


        // Check password
        val passwordString = binding.editTextTextPassword.text.toString()
        validPassword = validatePassword(passwordString)

        if (!validPassword || !validEmail){
            validForm = false
            return validForm
        }

        return validForm
    }

    fun validateEmail(email: String): Boolean{
        var validEmail: Boolean = true

        if (TextUtils.isEmpty(email)){
            binding.editTextTextEmailAddress.error = "Required"
            validEmail = false
        } else {

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.editTextTextEmailAddress.error = "Invalid"
                validEmail = false
            }
        }

        return validEmail
    }

    fun validatePassword(password: String): Boolean{

        var validPassword: Boolean = true

        if (TextUtils.isEmpty(password)){
            binding.editTextTextPassword.error = "Required"
            validPassword = false
            return validPassword
        }

        return validPassword
    }




}