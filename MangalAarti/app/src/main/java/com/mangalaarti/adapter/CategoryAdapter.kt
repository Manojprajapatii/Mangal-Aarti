package com.mangalaarti.adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mangalaarti.activity.AartiActivity
import com.mangalaarti.activity.AllAartiActivity
import com.mangalaarti.databinding.AartiRecyclerviewBinding
import com.mangalaarti.modal.CategoryModal

class CategoryAdapter(
    private val aartiActivity: AartiActivity,
    private var list: List<CategoryModal> // Changed to a mutable list to handle dynamic updates
) : RecyclerView.Adapter<CategoryAdapter.CatViewHolder>() {

    class CatViewHolder(val binding: AartiRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        return CatViewHolder(
            AartiRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val category = list[position]

        // Set the text with null safety
        holder.binding.aartiTextLable.text = category.name ?: "No Name"

        // Load the image with null safety
        category.image?.let {
            Glide.with(aartiActivity)
                .load(it)
                .into(holder.binding.aartiImage)
        }

        // Set click listener
        holder.binding.root.setOnClickListener {
            val intent = Intent(aartiActivity, AllAartiActivity::class.java)
            intent.putExtra("id", category.id)
            intent.putExtra("name", category.name)
            aartiActivity.startActivity(intent)
        }

    }



    // New method to update the data in the adapter dynamically
    fun updateData(newList: List<CategoryModal>) {
        list = newList
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
