package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityMainBinding
import com.example.taller3_firebase.databinding.MenuActivityBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange

class MenuActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: MenuActivityBinding
    lateinit var usuario: User
    private var users = emptyArray<User>()
    private lateinit var googleMap: GoogleMap
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val currentUser = auth.currentUser

        if (currentUser != null) {
            binding.usrEmail.setText(currentUser.email)
        }

        binding.logOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.modifyInfoButton.setOnClickListener {
            val intent = Intent(baseContext, ModifyInfo::class.java)
            startActivity(intent)
        }

        binding.userListButton.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            val newState = if (isChecked) "Disponible" else "No Disponible"

            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            if (userId != null) {
                val userRef = FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                userRef.update("state", newState)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }
            }
        }
        if (currentUser != null) {
            binding.usrEmail.text = currentUser.email

            val userDocument = firestore.collection("usuarios").document(currentUser.email!!)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userState = documentSnapshot.getString("state")

                    binding.switch1.isChecked = userState == "Disponible"
                    binding.usrState.text = userState

                    binding.switch1.setOnCheckedChangeListener { _, isChecked ->
                        val newState = if (isChecked) "Disponible" else "No Disponible"
                        userDocument.update("state", newState)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                            }
                    }
                }
            }.addOnFailureListener {
            }

            val availableUsers = ArrayList<User>()

            firestore.collection("usuarios")
                .whereEqualTo("state", "Disponible")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // Manejar el error
                        return@addSnapshotListener
                    }

                    for (doc in snapshot!!.documents) {
                        val userEmail = doc.getString("email")
                         Toast.makeText(this, "Usuario conectado: $userEmail", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val puntosDeInteres = leerPuntosDeInteresDesdeJSON()
        agregarMarcadoresDeInteres(puntosDeInteres)

        val ubicacionUsuario = LatLng(4.6, -74.090749)
        googleMap.addMarker(MarkerOptions().position(ubicacionUsuario).title("Mi ubicación"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionUsuario, 10f))
    }

    private fun leerPuntosDeInteresDesdeJSON(): List<Pair<LatLng, String>> {
        val puntosDeInteres = mutableListOf<Pair<LatLng, String>>()

        val inputStream = resources.openRawResource(R.raw.locations)
        val jsonText = inputStream.bufferedReader().use { it.readText() }

        val jsonObject = JSONObject(jsonText)
        val locationsArray = jsonObject.getJSONArray("locationsArray")

        for (i in 0 until locationsArray.length()) {
            val locationObject = locationsArray.getJSONObject(i)
            val latitude = locationObject.getDouble("latitude")
            val longitude = locationObject.getDouble("longitude")
            val nombre = locationObject.getString("name")

            puntosDeInteres.add(Pair(LatLng(latitude, longitude), nombre))
        }

        return puntosDeInteres
    }

    private fun agregarMarcadoresDeInteres(puntosDeInteres: List<Pair<LatLng, String>>) {
        for ((puntoDeInteres, nombre) in puntosDeInteres) {
            googleMap.addMarker(MarkerOptions().position(puntoDeInteres).title(nombre))
        }
    }



}
