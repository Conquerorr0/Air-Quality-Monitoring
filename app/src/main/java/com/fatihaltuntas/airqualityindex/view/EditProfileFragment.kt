package com.fatihaltuntas.airqualityindex.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.airqualityindex.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        loadUserData()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadUserData() {
        auth.currentUser?.let { user ->
            binding.nameEditText.setText(user.displayName)
            binding.emailEditText.setText(user.email)
        }
    }

    private fun setupListeners() {
        binding.changePhotoButton.setOnClickListener {
            // TODO: Fotoğraf seçme işlemi
            Toast.makeText(context, "Yakında eklenecek", Toast.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val newName = binding.nameEditText.text.toString()
        val newEmail = binding.emailEditText.text.toString()

        if (newName.isBlank() || newEmail.isBlank()) {
            Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        auth.currentUser?.let { user ->
            // İsim güncelleme
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Email güncelleme
                        if (user.email != newEmail) {
                            user.updateEmail(newEmail)
                                .addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(context, "Profil güncellendi", Toast.LENGTH_SHORT).show()
                                        findNavController().navigateUp()
                                    } else {
                                        Toast.makeText(context, "Email güncellenemedi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Profil güncellendi", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    } else {
                        Toast.makeText(context, "Profil güncellenemedi", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
} 