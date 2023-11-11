package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityMainBinding
import com.example.taller3_firebase.databinding.MenuActivityBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: MenuActivityBinding
    lateinit var usuario: User
    private var users = emptyArray<User>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val currentUser = auth.currentUser

        if (currentUser != null) {
            binding.usrEmail.setText(currentUser.email)
        }

        binding.logOutButton.setOnClickListener{
            auth.signOut()
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }

        binding.modifyInfoButton.setOnClickListener {
            val intent = Intent(baseContext, ModifyInfo::class.java)
            startActivity(intent)
        }

        if (currentUser != null) {
            // Step 1: Search for the user with the same email in the database
            databaseReference.orderByChild("email").equalTo(currentUser.email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapshot in snapshot.children) {
                            val userState = userSnapshot.child("state").getValue(String::class.java)

                            // Step 2: Set the state of the switch based on the retrieved user state
                            binding.switch1.isChecked = userState == "Disponible"
                            binding.usrState.text = userState

                            // Handle switch state change
                            binding.switch1.setOnCheckedChangeListener { _, isChecked ->
                                // Update the "state" property in the Realtime Database based on switch state
                                val newState = if (isChecked) "Disponible" else "No Disponible"
                                userSnapshot.child("state").ref.setValue(newState)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })

            // Step 3: Search for users whose state is "Disponible"
            val availableUsers = ArrayList<User>()

            databaseReference.orderByChild("state").equalTo("Disponible")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapshot in snapshot.children) {
                            val userEmail = userSnapshot.child("email").getValue(String::class.java)
                            val userFirstName = userSnapshot.child("firstName").getValue(String::class.java)
                            val userLastName = userSnapshot.child("lastName").getValue(String::class.java)
                            val userIdNumberStr = userSnapshot.child("idNumber").getValue(String::class.java)
                            val userLatitudeStr = userSnapshot.child("latitude").getValue(String::class.java)
                            val userLongitudeStr = userSnapshot.child("longitude").getValue(String::class.java)
                            val userStatus = userSnapshot.child("status").getValue(String::class.java)

                            // Parse to numbers
                            val userIdNumber = userIdNumberStr?.toBigIntegerOrNull() ?: 0
                            val userLatitude = userLatitudeStr?.toDoubleOrNull() ?: 0.0
                            val userLongitude = userLongitudeStr?.toDoubleOrNull() ?: 0.0

                            val user = User(
                                userEmail?: "",
                                userFirstName ?: "",
                                userLastName ?: "",
                                userIdNumber ?: 0,
                                userLatitude ?: 0.0,
                                userLongitude ?: 0.0,
                                userStatus ?: ""
                            )
                            availableUsers.add(user)
                        }


                        //Aqui user debería ser igual a un arreglo que retorna la conuslta a la base de datos

                        val users = availableUsers
                        val adapter = UserAdapter(baseContext, availableUsers)
                        binding.listView.adapter = adapter
                        binding.listView.setOnItemClickListener { parent, view, position, id ->
                            val selectedItem = parent.getItemAtPosition(position) as User
                            val latitud = selectedItem.latitude
                            val longitud = selectedItem.longitude
                            val intent = Intent(baseContext, MapsActivity::class.java )

                            intent.putExtra("latitud", latitud)
                            intent.putExtra("longitud", longitud)

                            startActivity(intent)

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }



    }

}
