package com.example.votersmanagementsystem.frontend

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.votersmanagementsystem.R
import com.example.votersmanagementsystem.api.RetrofitClient
import com.example.votersmanagementsystem.model.Voter
import com.example.votersmanagementsystem.model.FilterCriteria
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VoterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        val filterAgeEditText: EditText = findViewById(R.id.filterAgeEditText)
        val filterRegisteredEditText: EditText = findViewById(R.id.filterRegisteredEditText)
        val filterButton: Button = findViewById(R.id.filterButton)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VoterAdapter(emptyList())  // Initialize adapter with empty list
        recyclerView.adapter = adapter

        filterButton.setOnClickListener {
            val minAge = filterAgeEditText.text.toString().toIntOrNull()
            val registeredFilter = filterRegisteredEditText.text.toString().lowercase()

            if (minAge == null && registeredFilter !in listOf("true", "false", "")) {
                Toast.makeText(this, "Please enter valid filter criteria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registered = when (registeredFilter) {
                "true" -> "true"
                "false" -> "false"
                else -> null
            }

            val filterCriteria = FilterCriteria(minAge = minAge, maxAge = null, registered = registered)
            fetchFilteredVoters(filterCriteria)
        }
    }

    private fun fetchFilteredVoters(filterCriteria: FilterCriteria) {
        RetrofitClient.apiService.filterVoters(
            filterCriteria.minAge,
            filterCriteria.maxAge,
            filterCriteria.registered
        ).enqueue(object : Callback<List<Voter>> {
            override fun onResponse(call: Call<List<Voter>>, response: Response<List<Voter>>) {
                if (response.isSuccessful) {
                    val filteredVoters = response.body() ?: emptyList()
                    adapter.updateList(filteredVoters)
                    if (filteredVoters.isEmpty()) {
                        Toast.makeText(this@FilterActivity, "No voters found with the given criteria.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@FilterActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Voter>>, t: Throwable) {
                Toast.makeText(this@FilterActivity, "Error fetching data: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
