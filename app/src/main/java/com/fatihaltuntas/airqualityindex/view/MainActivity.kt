package com.fatihaltuntas.airqualityindex.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fatihaltuntas.airqualityindex.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var email : String
    private lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null) {
            val intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signInClicked(view : View) {
        email = binding.txtEmail.text.toString()
        password = binding.txtPassword.text.toString()

        if(email.equals("") || password.equals("")) {
            Toast.makeText(this, "E-posta yada şifre boş olamaz!", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this, AppActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Eposta yada şifre hatalı!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signUpClicked(view : View) {
        email = binding.txtEmail.text.toString()
        password = binding.txtPassword.text.toString()

        if(email.equals("") || password.equals("")) {
            Toast.makeText(this,"E-posta yada şifre boş olamaz!", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this, AppActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                println(it.localizedMessage)
                Toast.makeText(this,"E-posta adresi zaten başka bir hesap tarafından kullanılıyor.", Toast.LENGTH_LONG).show()
            }
        }
    }

}