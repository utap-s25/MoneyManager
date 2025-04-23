package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanager.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser ?: return@addOnCompleteListener
                        val userMap = hashMapOf("email" to user.email)
                        val db = FirebaseFirestore.getInstance()

                        db.collection(Constants.FIRESTORE_USERS_KEY)
                            .document(user.uid)
                            .set(userMap, SetOptions.merge())  // merge so you don’t overwrite existing fields
                            .addOnSuccessListener {
                                // Now retrieve the externalApiGuid
                                db.collection(Constants.FIRESTORE_USERS_KEY).document(user.uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            val guid = document.getString(Constants.FIRESTORE_MX_USER_ID_KEY)
                                            if (guid != null) {
                                                val prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE)
                                                prefs.edit().putString(Constants.MX_USER_GUID_KEY, guid).apply()
                                            }
                                        }

                                        // Navigate to MainActivity after everything is done
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}