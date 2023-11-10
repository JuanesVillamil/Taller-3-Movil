package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    /*
    private fun saveUserToDatabase(userId: String, firstName: String, lastName: String, idNumber: String, imageUrl: String, latitude: Double, longitude: Double) {
        val userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val newUser = User(firstName, lastName, idNumber, imageUrl, latitude, longitude)
        userReference.setValue(newUser)
    }

     */
}