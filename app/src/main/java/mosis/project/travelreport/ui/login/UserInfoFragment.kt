package mosis.project.travelreport.ui.login

import android.app.Activity
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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import mosis.project.travelreport.R
import mosis.project.travelreport.data.UserInfoData
import mosis.project.travelreport.databinding.FragmentUserInfoBinding
import mosis.project.travelreport.model.UserViewModel
import mosis.project.travelreport.ui.main.MainMenuActivity
import mosis.project.travelreport.utils.Constants
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class UserInfoFragment : Fragment() {

    private var uri: Uri? = null

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase

    //ViewModel
    private val userViewModel: UserViewModel by inject()


    //Binding
    private lateinit var binding: FragmentUserInfoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)

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
            startActivityForResult(cameraIntent, Constants.CAMERA_IMAGE)
        }
        binding.bAddImageFromGalery.setOnClickListener {
            var galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, Constants.GALLERY_IMAGE)

        }


        binding.bDone.setOnClickListener {


            val fullName = binding.etName.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val day = binding.dpDateOfBirth.dayOfMonth
            val month = binding.dpDateOfBirth.month + 1
            val year = binding.dpDateOfBirth.year

            val date = Date(year,month,day)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if (uri != null) {
                userViewModel.getUserId() {
                    it?.let { userId ->
                        userViewModel.addUserInfoData(
                            UserInfoData(
                                userId,
                                fullName,
                                phoneNumber,
                                sdf.format(date)
                            ), uri!!
                        ) { itError ->
                            if (itError == null) {
                                findNavController().navigate(R.id.action_UserInfoFragment_to_LoginFragment)
                            } else {
                                Toast.makeText(this.context, itError, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    }
                }

            } else {
                Toast.makeText(
                    this.context,
                    "User image cannot be empty.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageURI = data.data
            uri = imageURI!!
            binding.ivUserImage.setImageURI(imageURI)
            binding.ivUserImage.background = null
        }
        if (requestCode == Constants.CAMERA_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageData = data.extras?.get("data") as Bitmap
            uri = getImageUriFromBitmap(this.context!!, imageData)
            binding.ivUserImage.setImageURI(uri)
            binding.ivUserImage.background = null
        }
    }
}