package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityMainBinding
import com.example.taller3_firebase.databinding.MenuActivityBinding
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: MenuActivityBinding
    lateinit var usuario: User
    private var users = emptyArray<User>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter: ArrayAdapter<*>

        //Aqui user debería ser igual a un arreglo que retorna la conuslta a la base de datos
        usuario = User("Pepito", "Perez", 123, 4.6286389, -74.0646392, "S")
        val users = arrayOf(usuario)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        binding.listView.adapter = adapter
        binding.listView.setOnItemClickListener { parent, view, position, id ->
             val selectedItem = parent.getItemAtPosition(position) as User
             val latitud = selectedItem.latitude
             val longitud = selectedItem.longitude
             val intent = Intent(this, MapsActivity::class.java )

            intent.putExtra("latitud", latitud)
            intent.putExtra("longitud", longitud)

            startActivity(intent)

        }
    }
}