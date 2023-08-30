package mosis.project.travelreport.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import mosis.project.travelreport.data.LocationData
import mosis.project.travelreport.databinding.FragmentAddLocationBinding
import mosis.project.travelreport.location.LocationWizard
import mosis.project.travelreport.model.LocationViewModel
import mosis.project.travelreport.model.UserViewModel
import mosis.project.travelreport.utils.Constants.CAMERA_IMAGE
import mosis.project.travelreport.utils.Constants.GALLERY_IMAGE
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddLocationFragment : Fragment() {

    private lateinit var uri: Uri


    // ViewModel
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val userViewModel: UserViewModel by inject()

    // Binding
    private lateinit var binding: FragmentAddLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddLocationBinding.inflate(inflater, container, false)

        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                111
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bAddImageCamera.setOnClickListener {
            var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_IMAGE)

        }
        binding.bAddImageFromGalery.setOnClickListener {
            var galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_IMAGE)
        }

        binding.bAddLocation.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val rate = binding.etRate.text.toString().toInt()
            val imageId = System.currentTimeMillis().toString()
            val tag = binding.etTag.text.toString()
            val name = binding.etName.text.toString()
            Log.d("TAG_RATE", rate.toString())
            if (rate < 0 || rate > 10) {
                Toast.makeText(this.context, "Rate must be between 0 and 10", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            var lat: Double = 0.0
            var lon: Double = 0.0

            LocationWizard.instance?.findMostAccurateLocation()?.let {
                lat = it.latitude
                lon = it.longitude

            }
            var locationData: LocationData= LocationData()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Calendar.getInstance().time
            userViewModel.getUserEmail {
                it?.let { email->
                     locationData = LocationData(
                        UUID.randomUUID().toString(),
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        imageId,
                        description,
                        rate,
                        lat,
                        lon,
                        sdf.format(date),
                        tag,
                        name
                    )
                }
            }
            locationViewModel.addPin(locationData, uri) {
                if (it != null) {
                    Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                }
            }

            userViewModel.updateUserPost {
                if (it != null) {
                    Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                }
            }

            parentFragmentManager.popBackStack()
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            }
        }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageURI = data.data
            uri = imageURI!!
            binding.ivLocation.setImageURI(imageURI)
            binding.ivLocation.background = null
        }
        if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageData = data.extras?.get("data") as Bitmap
            uri = getImageUriFromBitmap(this.context!!, imageData)
            binding.ivLocation.setImageURI(uri)
            binding.ivLocation.background = null
        }

    }


}