package com.fatihaltuntas.airqualityindex.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserInfo()
        setupButtons()
    }

    private fun setupUserInfo() {
        auth.currentUser?.let { user ->
            binding.userName.text = user.displayName ?: "Kullanıcı"
            binding.userEmail.text = user.email
        }
    }

    private fun setupButtons() {
        // Profili Düzenle
        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Bildirim Ayarları
        binding.notificationSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationSettingsFragment)
        }

        // Tema Ayarları
        binding.themeSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_themeSettingsFragment)
        }

        // Çıkış Yap
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Çıkış Yap")
            .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Çıkış Yap") { _, _ ->
                logout()
            }
            .show()
    }

    private fun logout() {
        auth.signOut()
        // LoginActivity'ye yönlendir
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
} 