package com.mangalaarti.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mangalaarti.activity.AllAartiActivity
import com.mangalaarti.databinding.ItemaartiBinding
import com.mangalaarti.modal.AartiModal

class AartiAdapter(
    private val allAartiActivity: AllAartiActivity,
    private val aartiList: ArrayList<AartiModal>
) : RecyclerView.Adapter<AartiAdapter.AartiViewHolder>() {

    private var currentFontSize: Float = 19f // Default font size

    class AartiViewHolder(val binding: ItemaartiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AartiViewHolder {
        val binding = ItemaartiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AartiViewHolder(binding)
    }

    override fun getItemCount() = aartiList.size

    override fun onBindViewHolder(holder: AartiViewHolder, position: Int) {
        // Retrieve the data from the list
        val aartiData = aartiList[position].data

        // Replace symbols for formatting
        val processedText = aartiData
            ?.replace("||", "<br><br>") // Double vertical bar will be replaced with double line break, ending the line
            ?.replace("|", "<br>") // Single vertical bar will be replaced with a single line break
            ?.replace(",", ",&nbsp;&nbsp;") // Keep comma and add some space after it
            ?.replace("।।", "।।<br><br>") // Ensure '।।' ends the line and starts a new line with space
            ?.replace("।", "।<br>") // Ensure '।' ends the line and starts a new line

        // Set the formatted text with HTML formatting
        holder.binding.itemAarti.text = Html.fromHtml(processedText, Html.FROM_HTML_MODE_LEGACY)

        // Apply the current font size to itemAarti
        holder.binding.itemAarti.textSize = currentFontSize
    }

    // Method to increase the font size
    fun increaseFontSize() {
        currentFontSize += 2f
        notifyDataSetChanged() // Notify the adapter to refresh the views
    }

    // Method to decrease the font size
    fun decreaseFontSize() {
        if (currentFontSize > 13f) { // Prevent font size from becoming too small
            currentFontSize -= 2f
            notifyDataSetChanged() // Notify the adapter to refresh the views
        }
    }

    // Method to get all Aarti text for sharing
    fun getAllAartiText(): String {
        return aartiList.joinToString(separator = "\n\n") { it.data ?: "" }
    }
}
