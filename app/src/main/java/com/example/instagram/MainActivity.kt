package com.example.instagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var etDescription: EditText
    lateinit var btTakePicture: Button
    lateinit var btLogOut: Button
    lateinit var btSubmit: Button
    lateinit var ivPicture: ImageView

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etDescription = findViewById(R.id.et_description)
        btTakePicture = findViewById(R.id.bt_take_picture)
        btLogOut = findViewById(R.id.bt_log_out)
        btSubmit = findViewById(R.id.bt_submit)
        ivPicture = findViewById(R.id.iv_picture)

        btLogOut.setOnClickListener {
            ParseUser.logOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btSubmit.setOnClickListener {
            //send post to server without img
            //Get description that they have inputted
            val description = etDescription.text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Toast.makeText(this, "No image was added", Toast.LENGTH_SHORT).show()
            }
        }

        btTakePicture.setOnClickListener {
            //launch camera to let user take pic
            onLaunchCamera()
        }

        //1. Setting the description of post
        //2. Btn to launc camera to take pic
        //3. iv to show pic that user has taken
        //4. bt to save and send post to parse

        queryPosts()
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
                //todo show toast to show err
            } else {
                Log.i(TAG, "Successfully saved post")
                //reset et and iv
            }
        }
    }

    //Query for posts in our server
    private fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.findInBackground { posts, e ->
            if (e != null) {
                //Something went wrong
                Log.e(TAG, "Error fetching posts")
            } else {
                if (posts != null) {
                    for (post in posts) {
                        Log.i(
                            TAG,
                            "Post: ${post.getDescription()}, username: ${post.getUser()?.username}"
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPicture.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
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
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

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