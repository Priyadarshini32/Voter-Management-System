package com.example.votersmanagementsystem.frontend

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.votersmanagementsystem.api.RetrofitClient
import com.example.votersmanagementsystem.model.Voter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root Layout - Center everything
        val rootLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER  // Center everything
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.parseColor("#F5F5F5")) // Light gray background
        }

        // CardView for displaying analysis
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 32, 32, 32)
            }
            radius = 24f
            cardElevation = 12f
            setCardBackgroundColor(Color.WHITE)
        }

        // TextView inside the CardView
        val resultTextView = TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(48, 48, 48, 48)
            textSize = 18f
            setTextColor(Color.parseColor("#333333"))
            setLineSpacing(12f, 1.2f) // Space between lines
            gravity = Gravity.CENTER
            // Make the text bold
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Add TextView to CardView
        cardView.addView(resultTextView)

        // Add CardView to Root Layout
        rootLayout.addView(cardView)

        // Set the dynamically created layout as the content view
        setContentView(rootLayout)

        // Fetch and analyze voter data
        fetchRegisteredVoters { registeredVoters ->
            val averageAge = calculateAverageAge(registeredVoters)
            val ageDistribution = calculateAgeDistribution(registeredVoters)
            val turnoutPrediction = predictTurnout(registeredVoters)

            // Format the analysis text with bold using HTML formatting
            val analysisText = """
                <b>📊 Voter Analysis Results</b><br><br> <!-- Line break after heading -->
                
                <b>Total Registered Voters:</b> ${registeredVoters.size}<br> <!-- Line break here -->
                
                <b>Predicted Turnout Rate:</b> ${"%.2f".format(turnoutPrediction)}%<br>
                
                <b> Average Age of Registered Voters:</b> $averageAge <br>
                
                <b>Age Group Distribution:</b><br>
                🔹 18-25: ${ageDistribution["18-25"]} voters<br>
                🔹 26-40: ${ageDistribution["26-40"]} voters<br>
                🔹 41-60: ${ageDistribution["41-60"]} voters<br>
                🔹 60+: ${ageDistribution["60+"]} voters<br>
            """.trimIndent()

            // Display formatted bold text inside the Card
            resultTextView.text = Html.fromHtml(analysisText)
        }
    }

    // Fetch only registered voters from the database
    private fun fetchRegisteredVoters(callback: (List<Voter>) -> Unit) {
        RetrofitClient.apiService.getVoters().enqueue(object : Callback<List<Voter>> {
            override fun onResponse(call: Call<List<Voter>>, response: Response<List<Voter>>) {
                if (response.isSuccessful) {
                    val voters = response.body()?.filter { it.registered } ?: emptyList()
                    callback(voters)
                } else {
                    Toast.makeText(this@AnalysisActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Voter>>, t: Throwable) {
                Toast.makeText(this@AnalysisActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Tail-recursive function to calculate average age
    private tailrec fun calculateAverageAge(voters: List<Voter>, sum: Int = 0, count: Int = 0): Int {
        return if (voters.isEmpty()) {
            if (count == 0) 0 else sum / count
        } else {
            calculateAverageAge(voters.drop(1), sum + voters.first().age, count + 1)
        }
    }

    // Recursive function to group voters by age category
    private fun calculateAgeDistribution(voters: List<Voter>, ageGroups: Map<String, Int> = mapOf("18-25" to 0, "26-40" to 0, "41-60" to 0, "60+" to 0)): Map<String, Int> {
        return if (voters.isEmpty()) {
            ageGroups
        } else {
            val updatedGroups = when (val age = voters.first().age) {
                in 18..25 -> ageGroups.plus("18-25" to (ageGroups["18-25"]!! + 1))
                in 26..40 -> ageGroups.plus("26-40" to (ageGroups["26-40"]!! + 1))
                in 41..60 -> ageGroups.plus("41-60" to (ageGroups["41-60"]!! + 1))
                else -> ageGroups.plus("60+" to (ageGroups["60+"]!! + 1))
            }
            calculateAgeDistribution(voters.drop(1), updatedGroups)
        }
    }

    // Function to predict voter turnout (uses a simple formula, assuming 75% turnout rate)
    private fun predictTurnout(voters: List<Voter>): Double {
        val turnoutRate = 75.0  // 75% assumed turnout rate
        return if (voters.isNotEmpty()) (voters.size * turnoutRate) / 100 else 0.0
    }
}
