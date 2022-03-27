package com.example.instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var signUpButton: Button
    lateinit var etUsername: TextInputEditText
    lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Check if user is logged in
        //If they are, navigate to MainActivity
        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        loginButton = findViewById(R.id.bt_login)
        signUpButton = findViewById(R.id.bt_sign_up)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)

        loginButton.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            loginUser(username, password)
        }

        signUpButton.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            signUpUser(username, password)
        }
    }

    private fun loginUser(username: String, password: String) {
        ParseUser.logInInBackground(
            username, password, ({ user, e ->
                if (user != null) {
                    goToMainActivity()
                } else {
                    e.printStackTrace()
                    Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show()
                }
            })
        )
    }

    private fun signUpUser(username: String, password: String) {
        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                //navigate to mainactivity
                loginUser(username, password)
            } else {
                e.printStackTrace()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}