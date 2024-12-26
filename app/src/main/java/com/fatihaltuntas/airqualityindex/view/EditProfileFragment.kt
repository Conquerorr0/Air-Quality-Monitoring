package com.fatihaltuntas.airqualityindex.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.FragmentEditProfileBinding
import com.fatihaltuntas.airqualityindex.util.PreferencesManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Kamera izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(context, "Galeri izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                loadImage(uri)
                saveImagePath(uri.toString())
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val copiedUri = copyImageToAppStorage(uri)
                loadImage(copiedUri)
                saveImagePath(copiedUri.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadUserData() {
        binding.apply {
            nameEditText.setText(preferencesManager.getUserName())
            emailEditText.setText(preferencesManager.getUserEmail())
            emailEditText.isEnabled = false // Email düzenlemeyi devre dışı bırak
            
            val savedImagePath = preferencesManager.getProfileImagePath()
            if (!savedImagePath.isNullOrEmpty()) {
                try {
                    loadImage(Uri.parse(savedImagePath))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            changePhotoButton.setOnClickListener {
                showImagePickerDialog()
            }

            saveButton.setOnClickListener {
                saveUserData()
            }
        }
    }

    private fun showImagePickerDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_image_picker, null)

        view.findViewById<View>(R.id.btnCamera).setOnClickListener {
            dialog.dismiss()
            checkCameraPermission()
        }

        view.findViewById<View>(R.id.btnGallery).setOnClickListener {
            dialog.dismiss()
            checkGalleryPermission()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(context, "Kamera izni gerekli", Toast.LENGTH_SHORT).show()
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            openGallery()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(context, "Galeri izni gerekli", Toast.LENGTH_SHORT).show()
                    galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            takePictureLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Kamera açılırken bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir("Pictures")
        return File.createTempFile(imageFileName, ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun copyImageToAppStorage(uri: Uri): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir("Pictures")
        val newFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            newFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            newFile
        )
    }

    private fun loadImage(uri: Uri) {
        try {
            Glide.with(requireContext())
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(binding.profileImage)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Fotoğraf yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImagePath(path: String) {
        preferencesManager.setProfileImagePath(path)
    }

    private fun saveUserData() {
        val name = binding.nameEditText.text.toString().trim()

        if (name.isBlank()) {
            Toast.makeText(context, "Lütfen isim alanını doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        preferencesManager.setUserName(name)

        Toast.makeText(context, "Değişiklikler kaydedildi", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
} 