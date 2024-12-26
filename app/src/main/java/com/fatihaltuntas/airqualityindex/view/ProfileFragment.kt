package com.fatihaltuntas.airqualityindex.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.fatihaltuntas.airqualityindex.util.PreferencesManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        // Her görünür olduğunda kullanıcı verilerini yenile
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserData() {
        binding.apply {
            userName.text = preferencesManager.getUserName()
            userEmail.text = preferencesManager.getUserEmail()

            // Profil fotoğrafını yükle
            val savedImagePath = preferencesManager.getProfileImagePath()
            if (!savedImagePath.isNullOrEmpty()) {
                try {
                    loadProfileImage(Uri.parse(savedImagePath))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadProfileImage(uri: Uri) {
        view?.let { view ->
            try {
                Glide.with(view)
                    .load(uri)
                    .circleCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(binding.profileImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            editProfileButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }

            notificationSettingsButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_notificationSettingsFragment)
            }

            themeSettingsButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_themeSettingsFragment)
            }

            logoutButton.setOnClickListener {
                showLogoutConfirmationDialog()
            }
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