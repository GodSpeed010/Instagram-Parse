package com.example.instagram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.instagram.LoginActivity
import com.example.instagram.Post
import com.example.instagram.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

class ComposeFragment : Fragment() {

    lateinit var etDescription: EditText
    lateinit var btTakePicture: Button
    lateinit var btLogOut: Button
    lateinit var btSubmit: Button
    lateinit var ivPicture: ImageView

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etDescription = view.findViewById(R.id.et_description)
        btTakePicture = view.findViewById(R.id.bt_take_picture)
        btLogOut = view.findViewById(R.id.bt_log_out)
        btSubmit = view.findViewById(R.id.bt_submit)
        ivPicture = view.findViewById(R.id.iv_picture)

        btLogOut.setOnClickListener {
            ParseUser.logOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        btSubmit.setOnClickListener {
            //send post to server without img
            //Get description that they have inputted
            val description = etDescription.text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Toast.makeText(requireContext(), "No image was added", Toast.LENGTH_SHORT).show()
            }
        }

        btTakePicture.setOnClickListener {
            //launch camera to let user take pic
            onLaunchCamera()
        }
    }

    private fun submitPost(description: String, user: ParseUser, photoFile: File) {
        // Create the post obj
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(photoFile))
        post.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving post")
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error sending post", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Successfully saved post")
                Toast.makeText(requireContext(), "Sent Post!", Toast.LENGTH_SHORT).show()
                //reset et and iv
            }
        }
    }

    private fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(
                    requireContext(),
                    "com.codepath.fileprovider",
                    photoFile!!
                )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPicture.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}