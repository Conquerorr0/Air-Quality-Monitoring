package com.fatihaltuntas.airqualityindex.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.ActivityAppBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding
    private var navHostFragment: NavHostFragment ?= null
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment!!.navController
        NavigationUI.setupWithNavController(binding.bottomNavgationView, navController)
    }

    fun signout(item: MenuItem) {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}