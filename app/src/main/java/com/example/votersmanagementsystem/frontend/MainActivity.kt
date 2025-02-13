package com.example.votersmanagementsystem.frontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.votersmanagementsystem.R
import com.example.votersmanagementsystem.model.Voter
import com.example.votersmanagementsystem.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// MainActivity inherits from AppCompatActivity,
class MainActivity : AppCompatActivity() {

    private lateinit var votersRecyclerView: RecyclerView
    private lateinit var adapter: VoterAdapter
    private val votersList = mutableListOf<Voter>()

    // Activity Result Launcher to refresh data
    private val registerVoterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchVoters()  // Refresh voter list when returning from RegisterVoterActivity
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize RecyclerView
        votersRecyclerView = findViewById(R.id.votersRecyclerView)
        votersRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VoterAdapter(votersList)
        votersRecyclerView.adapter = adapter

        // Buttons for navigation
        val registerButton: Button = findViewById(R.id.registerButton)
        val filterButton: Button = findViewById(R.id.filterButton)
        val analyzeButton: Button = findViewById(R.id.analyzeButton)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterVoterActivity::class.java)
            registerVoterLauncher.launch(intent)  // Launch RegisterVoterActivity
        }

        filterButton.setOnClickListener { navigateToActivity(FilterActivity::class.java) }
        analyzeButton.setOnClickListener { navigateToActivity(AnalysisActivity::class.java) }

        // Fetch voters
        fetchVoters()
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun fetchVoters() {
        RetrofitClient.apiService.getVoters().enqueue(object : Callback<List<Voter>> {
            override fun onResponse(call: Call<List<Voter>>, response: Response<List<Voter>>) {
                if (response.isSuccessful) {
                    votersList.clear()
                    votersList.addAll(response.body() ?: emptyList())
                    adapter.updateList(votersList)
                }
            }

            override fun onFailure(call: Call<List<Voter>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
