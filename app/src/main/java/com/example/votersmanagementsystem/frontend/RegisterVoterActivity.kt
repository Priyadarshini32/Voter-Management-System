package com.example.votersmanagementsystem.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.votersmanagementsystem.R
import com.example.votersmanagementsystem.api.ApiService
import com.example.votersmanagementsystem.api.RetrofitClient
import com.example.votersmanagementsystem.model.Voter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterVoterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var registerButton: Button

    private val apiService: ApiService by lazy {
        RetrofitClient.apiService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Ensure this is the correct XML file

        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val age = ageEditText.text.toString().toIntOrNull()

            if (age == null) {
                Toast.makeText(this, "Please enter a valid age!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validateInput(name, age)) {
                registerVoter(name, age)
            } else {
                Toast.makeText(this, "Invalid input! Name must be at least 3 characters and age must be 18 or above.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to validate input
    private fun validateInput(name: String, age: Int): Boolean {
        return isValidName(name) && age >= 18
    }

    // Lambda function for name validation
    private val isValidName: (String) -> Boolean = { name -> name.length > 2 }

    // Function to register voter via API
    private fun registerVoter(name: String, age: Int) {
        val voter = Voter(name = name, age = age, registered = true)

        apiService.registerVoter(voter).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterVoterActivity, "Voter Registered!", Toast.LENGTH_SHORT).show()
                    nameEditText.text.clear()
                    ageEditText.text.clear()

                    // Return result OK and finish activity
                    setResult(RESULT_OK, Intent())
                    finish()
                } else {
                    Toast.makeText(this@RegisterVoterActivity, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@RegisterVoterActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
