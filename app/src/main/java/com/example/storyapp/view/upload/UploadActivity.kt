package com.example.storyapp.view.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityUploadBinding
import com.example.storyapp.model.UserPreference
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var getFile: File? = null
    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasCameraPermission()) {
            requestCameraPermission()
        }

        uploadViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener {
            uploadImage()
        }
        binding.checkBoxUseCurrentLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!hasLocationPermission()) {
                    requestLocationPermission()
                } else {
                    getLocation()
                }
                binding.latitudeText.visibility = View.VISIBLE
                binding.longitudeText.visibility = View.VISIBLE
                binding.addressText.visibility = View.VISIBLE
            } else {
                binding.latitudeText.visibility = View.GONE
                binding.longitudeText.visibility = View.GONE
                binding.addressText.visibility = View.GONE
            }
        }

        uploadViewModel.uploadStory.observe(this) {
            if (it != null) {
                navigateToStoryList()
                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA_PERMISSION
        )
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_LOCATION_PERMISSION
        )
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                "com.example.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationProvider: String = LocationManager.NETWORK_PROVIDER
        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)
        latitude = lastKnownLocation?.latitude
        longitude = lastKnownLocation?.longitude

        if (latitude != null && longitude != null && binding.checkBoxUseCurrentLocation.isChecked) {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            val address =
                addresses?.get(0)?.getAddressLine(0)

            binding.latitudeText.text = "${getString(R.string.latitude)} $latitude"
            binding.longitudeText.text = "${getString(R.string.longitude)} $longitude"
            binding.addressText.text = address
        } else {
            Toast.makeText(this, getString(R.string.location_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            if (binding.checkBoxUseCurrentLocation.isChecked) {
                getLocation()
            }

            uploadViewModel.getToken().observe(this) { token ->

                uploadViewModel.uploadStory(token, description, imageMultipart, latitude, longitude)
            }
        } else {
            Toast.makeText(
                this@UploadActivity,
                getString(R.string.file_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToStoryList() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                Glide.with(this)
                    .load(file)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(binding.previewImageView)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 1
        private const val REQUEST_CODE_LOCATION_PERMISSION = 2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.camera_permission_required),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
            REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_required),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
