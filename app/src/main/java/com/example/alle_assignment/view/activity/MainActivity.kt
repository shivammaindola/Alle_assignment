package com.example.alle_assignment.view.activity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alle_assignment.R
import com.example.alle_assignment.view.fragment.InfoFragment
import com.example.alle_assignment.view.fragment.ShareFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 1
    }
    private lateinit var shareFragment: ShareFragment
    private lateinit var infoFragment: InfoFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        }

        // Initialize fragments
        shareFragment = ShareFragment()
        infoFragment = InfoFragment()

        // Initially show ShareFragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, shareFragment, "ShareFragment")
            .commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.shareFragment -> showShareFragment()
                R.id.infoFragment -> showInfoFragment()
            }
            true
        }
    }

    private fun showShareFragment() {
        supportFragmentManager.beginTransaction().apply {
            if (!shareFragment.isAdded) {
                add(R.id.fragment_container, shareFragment, "ShareFragment")
            }
            addToBackStack(null)
            show(shareFragment)
            hide(infoFragment)
            commit()
        }
    }

    private fun showInfoFragment() {
        supportFragmentManager.beginTransaction().apply {
            if (!infoFragment.isAdded) {
                add(R.id.fragment_container, infoFragment, "InfoFragment")
            }
            addToBackStack(null)
            hide(shareFragment)
            show(infoFragment)
            commit()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can notify your fragments or ViewModel if needed
        } else {
            // Permission denied, handle the denial appropriately
        }
    }

}
