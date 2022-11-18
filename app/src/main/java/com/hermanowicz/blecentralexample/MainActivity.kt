package com.hermanowicz.blecentralexample

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hermanowicz.blecentralexample.databinding.ActivityMainBinding
import com.juul.kable.Advertisement
import com.juul.kable.Scanner
import com.juul.kable.peripheral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val scanner = Scanner()
    private val foundDevices = hashMapOf<String, Advertisement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()

        binding.bleDevices.layoutManager = LinearLayoutManager(this)
    }

    private fun setListeners() {
        binding.findDevicesButton.setOnClickListener { requestPermissionsAndFindDevices() }
    }

    private fun requestPermissionsAndFindDevices() {
        val permissions = arrayOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT, ACCESS_FINE_LOCATION)
        when {
            ContextCompat.checkSelfPermission(this, BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED-> {
                findDevices()
            }
            shouldShowRequestPermissionRationale(BLUETOOTH_SCAN) -> {
                showPermissionNeededToast()
            }
            else -> {
                ActivityCompat.requestPermissions(this, permissions, 20)
            }
        }
    }

    private fun findDevices() {
        scope.launch {
            scanner.advertisements.collect {advertisement ->
                foundDevices[advertisement.address] = advertisement
                runOnUiThread {
                    val adapter = PeripheralsAdapter(foundDevices, PeripheralsAdapter.OnClickListener {
                        scope.launch {
                            val peripheral = scope.peripheral(advertisement)
                            peripheral.connect()
                        }
                        showConnectionSuccessToast(advertisement)
                    })
                    binding.bleDevices.adapter = adapter
                }
            }
        }
    }

    private fun showConnectionSuccessToast(advertisement: Advertisement) {
        val statement = advertisement.address + " connected"
        Toast.makeText(this@MainActivity, statement, Toast.LENGTH_LONG).show()
    }

    private fun showPermissionNeededToast() {
        Toast.makeText(this, getString(R.string.permissions), Toast.LENGTH_LONG).show()
    }
}