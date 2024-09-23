package com.mangalaarti.activity

import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mangalaarti.adapter.CategoryAdapter
import com.mangalaarti.databinding.ActivityAartiBinding
import com.mangalaarti.modal.CategoryModal
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation

class AartiActivity : AppCompatActivity() {

    private lateinit var searchbarIcon: ImageView
    private lateinit var searchBar: MaterialSearchBar
    lateinit var db: FirebaseFirestore
    private lateinit var fullList: List<CategoryModal>
    private lateinit var adapter: CategoryAdapter
    private lateinit var englishHindiTranslator: Translator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAartiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic("notification")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Subscription successful
                    Log.d("FCM", "Successfully subscribed to topic: notification")
                } else {
                    // Subscription failed
                    Log.e("FCM", "Failed to subscribe to topic: notification", task.exception)
                }
            }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        searchbarIcon = binding.searchbarIcon
        searchBar = binding.searchBar

        // Initialize ML Kit translator
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI)
            .build()
        englishHindiTranslator = Translation.getClient(options)

        // Download the translation model
        englishHindiTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                // Model downloaded successfully, you can now perform translation.
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }

        // Fetch data from Firestore and update RecyclerView
        db.collection("Aartitypes").addSnapshotListener { value, error ->
            if (error != null) {
                // Handle error (optional)
                return@addSnapshotListener
            }

            val list = value?.toObjects(CategoryModal::class.java) ?: emptyList()
            fullList = list

            binding.aartiRecyclerview.layoutManager = LinearLayoutManager(this)
            adapter = CategoryAdapter(this, list)
            binding.aartiRecyclerview.adapter = adapter
        }

        // Set click listener for searchbarIcon
        searchbarIcon.setOnClickListener {
            // Show the MaterialSearchBar and hide the searchbarIcon
            searchBar.visibility = View.VISIBLE
            searchbarIcon.visibility = View.GONE
            searchBar.requestFocus()
        }

        // Handle search actions
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    // Hide the MaterialSearchBar and show the searchbarIcon when search is closed
                    hideSearchBar()
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let {
                    performSearch(it.toString())
                }
            }

            override fun onButtonClicked(buttonCode: Int) {
                // Handle button clicks if needed
            }
        })

        // Set a global touch listener on the root view
        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN && searchBar.visibility == View.VISIBLE) {
                // Check if the touch is outside the search bar
                val location = IntArray(2)
                searchBar.getLocationOnScreen(location)
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                val left = location[0]
                val top = location[1]
                val right = left + searchBar.width
                val bottom = top + searchBar.height

                if (x < left || x > right || y < top || y > bottom) {
                    // Hide the search bar and show the search icon
                    hideSearchBar()
                }
            }
            false // Return false to allow other touch events to be processed
        }
    }

    private fun hideSearchBar() {
        searchBar.visibility = View.GONE
        searchbarIcon.visibility = View.VISIBLE
        adapter.updateData(fullList) // Show full list when search is closed
    }

    private fun performSearch(query: String) {
        // Translate the query to Hindi
        englishHindiTranslator.translate(query)
            .addOnSuccessListener { translatedQuery ->
                // Perform search with the translated query
                val filteredList = fullList.filter {
                    it.name?.contains(translatedQuery, ignoreCase = true) == true ||
                            it.name?.contains(query, ignoreCase = true) == true
                }

                if (filteredList.isNotEmpty()) {
                    // Update the RecyclerView with the filtered list
                    adapter.updateData(filteredList)
                } else {
                    // If no results found, show an empty list
                    adapter.updateData(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }

    override fun onDestroy() {
        // Close the translator to release resources
        englishHindiTranslator.close()
        super.onDestroy()
    }
}
