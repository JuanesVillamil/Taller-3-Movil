package com.example.taller3_firebase
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    private lateinit var trackedUser: User
    private lateinit var trackedUserLocationListener: ListenerRegistration
    private lateinit var polyline: Polyline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        trackedUser = try {
            intent?.getParcelableExtra("user") ?: throw IllegalArgumentException("User object is null")
        } catch (e: Exception) {
            Log.e("MapActivity", "Error al obtener el usuario: ${e.message}")
            finish()
            return
        }

        try {
            db = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("MapActivity", "Error al inicializar Firestore: ${e.message}")
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        trackedUserLocationListener = db.collection("usuarios").document(trackedUser.id)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val updatedTrackedUser = snapshot.toObject(User::class.java)
                    if (updatedTrackedUser != null) {
                        trackedUser = updatedTrackedUser
                    }
                }
            }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val javerianaBogota = LatLng(4.6382, -74.0838)
        val mosqueraCundinamarca = LatLng(4.7059, -74.2305)

        googleMap.addMarker(MarkerOptions()
            .position(mosqueraCundinamarca)
            .title("Posicion Actual")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))


        googleMap.addMarker(MarkerOptions()
            .position(javerianaBogota)
            .title("Posición Usuario Seguido")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

        val polylineOptions = PolylineOptions()
            .add(javerianaBogota, mosqueraCundinamarca)
            .width(5f)
            .color(Color.BLUE)

        googleMap.addPolyline(polylineOptions)

        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(javerianaBogota)
        boundsBuilder.include(mosqueraCundinamarca)
        val bounds = boundsBuilder.build()
        val padding = 100
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.moveCamera(cameraUpdate)
    }



    private fun simulateLocationUpdate() {

        val newLatitude = trackedUser.latitude.toDouble() + 0.001
        val newLongitude = trackedUser.longitude.toDouble() + 0.001
        val simulatedUserLocation = LatLng(newLatitude, newLongitude)

        googleMap.clear()

        val trackedUserMarker = googleMap.addMarker(MarkerOptions()
            .position(simulatedUserLocation)
            .title("Usuario Seguido"))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLocation = LatLng(location.latitude, location.longitude)

                        val distance = calculateDistance(
                            simulatedUserLocation.latitude,
                            simulatedUserLocation.longitude,
                            userLocation.latitude,
                            userLocation.longitude
                        )

                        val points = mutableListOf(simulatedUserLocation, userLocation)
                        polyline.points = points

                        // Puedes hacer algo con la ubicación actual y la distancia
                        Log.d("MapActivity", "Ubicación actual del usuario que realiza el seguimiento: $userLocation")
                        Log.d("MapActivity", "Distancia en línea recta: $distance metros")

                        val boundsBuilder = LatLngBounds.Builder()
                        if (trackedUserMarker != null) {
                            boundsBuilder.include(simulatedUserLocation)
                        }
                        boundsBuilder.include(userLocation)
                        val bounds = boundsBuilder.build()
                        val padding = 100
                        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                        googleMap.moveCamera(cameraUpdate)
                    }
                }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    override fun onDestroy() {
        super.onDestroy()
        trackedUserLocationListener.remove()
    }
}
