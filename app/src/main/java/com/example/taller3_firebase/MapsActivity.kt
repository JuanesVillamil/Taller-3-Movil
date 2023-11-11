package com.example.taller3_firebase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.taller3_firebase.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import java.util.logging.Logger

var LongitudDisponible: Double = 0.0
var LatitudDisponible: Double = 0.0
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        val REQUEST_CODE_LOCATION = 0
        val TAG: String = MapsActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var start: String =""
    lateinit var fusedLocationClient: FusedLocationProviderClient


    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        LongitudDisponible= intent.getDoubleExtra("longitud", 0.0)
        LatitudDisponible = intent.getDoubleExtra("latitud", 0.0)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        verifyPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "El permiso es requerido para poder mostrar tu ubicación en el mapa")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val ubicacion = LatLng(location.latitude, location.longitude)

                // Agregar el marcador al mapa con el icono personalizado y tamaño fijo
                mMap.addMarker(
                    MarkerOptions().position(ubicacion)
                        .title("Marker in my actual position ${location.latitude} ${location.longitude}")
                        .snippet("My location")
                        .alpha(0.9f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )

               // start = "${location.longitude},${location.latitude}"
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion))
                val ubicacionDisponible = LatLng(LatitudDisponible, LongitudDisponible)
                mMap.addMarker(
                    MarkerOptions().position(ubicacionDisponible)
                        .title("Posicion disponilbe")
                        .snippet("disponible")
                        .alpha(0.9f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            }

        }
    // Add a marker in Sydney and move the camera
        //val posUser = LatLng()
        //mMap.addMarker(MarkerOptions().position(posUser).title("Marker in Sydney"))
        ///mMap.moveCamera(CameraUpdateFactory.newLatLng(posUser))

    }
    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // We display a snackbar with the justification for the permission, and once it disappears, we request it again.
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }
    fun updateUI(permission: Boolean) {
        if (permission) {
            // granted
            logger.info("Permission granted")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            mMap.isMyLocationEnabled = true


            var locationCallback: LocationCallback
            //var polylineOptions = PolylineOptions()


            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.forEach { location ->
                        // Obtén la nueva ubicación
                        val latLng = LatLng(location.latitude, location.longitude)
                        start = "${location.longitude},${location.latitude}"



                        // Mueve la cámara al nuevo punto
                        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            }

            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // Intervalo de actualización de ubicación en milisegundos
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            // Verifica la configuración de ubicación
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // Configuración de ubicación aceptada, comienza la actualización
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }

        } else {
            logger.warning("Permission denied")
        }
    }
}