package com.mangalaarti.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mangalaarti.adapter.AartiAdapter
import com.mangalaarti.databinding.ActivityAllAartiBinding
import com.mangalaarti.modal.AartiModal

@Suppress("DEPRECATION")
class AllAartiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllAartiBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllAartiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")

        // Set up buttons
        binding.btnIncrease.setOnClickListener {
            (binding.rcvAarti.adapter as AartiAdapter).increaseFontSize()
        }

        binding.btnDecrease.setOnClickListener {
            (binding.rcvAarti.adapter as AartiAdapter).decreaseFontSize()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // Set category name
        binding.catName.text = name.toString()

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Fetch Aarti data and set up RecyclerView
        db.collection("Aartitypes").document(id!!).collection("hindi")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val aartiList = arrayListOf<AartiModal>()
                val data = value?.toObjects(AartiModal::class.java)
                aartiList.addAll(data!!)
                binding.rcvAarti.layoutManager = LinearLayoutManager(this)
                binding.rcvAarti.adapter = AartiAdapter(this, aartiList)
            }

        // Set up share button
        binding.btnShareAarti.setOnClickListener {
            shareAartiContent()
        }
    }

    private fun shareAartiContent() {
        // Collect Aarti data to share
        val aartiContent = (binding.rcvAarti.adapter as AartiAdapter).getAllAartiText()

        // Create share intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, aartiContent)
            type = "text/plain"
        }

        // Show the system share sheet
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}
