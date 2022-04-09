package com.frybits.squarechallenge.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frybits.squarechallenge.R
import com.frybits.squarechallenge.databinding.ViewEmployeeBinding
import com.frybits.squarechallenge.models.EmployeeData
import com.frybits.squarechallenge.models.EmployeeDataList
import com.frybits.squarechallenge.viewmodels.EmployeeDirectoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Recycler view adapter for the employee directory
 */
class EmployeeAdapter(
    private val scope: CoroutineScope,
    private val employeeDirectoryViewModel: EmployeeDirectoryViewModel
) : ListAdapter<EmployeeData, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

    inner class EmployeeViewHolder(private val viewEmployeeBinding: ViewEmployeeBinding) : RecyclerView.ViewHolder(viewEmployeeBinding.root) {

        // Used to ensure we don't continue loading old data if the view holder is recycled.
        private var currentJob: Job? = null

        fun setEmployeeData(employeeData: EmployeeData) {
            currentJob?.cancel() // Cancel job if still running
            currentJob = scope.launch { // Load employee data in a coroutine
                viewEmployeeBinding.nameTextView.text = employeeData.full_name
                viewEmployeeBinding.teamTextView.text = employeeData.team
                viewEmployeeBinding.bioTextView.text = employeeData.biography
                viewEmployeeBinding.profileImageView.setImageResource(R.drawable.ic_baseline_person_outline_24) // Set default image placeholder

                try {
                    viewEmployeeBinding.imageLoadingProgressBar.visibility = View.VISIBLE // Load progress bar for image view
                    val image = employeeDirectoryViewModel.getEmployeeSmallImage(employeeData) ?: return@launch
                    viewEmployeeBinding.profileImageView.setImageBitmap(image)
                } finally { // Regardless of result, hide the progressbar
                    viewEmployeeBinding.imageLoadingProgressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(ViewEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employeeData = getItem(position)
        holder.setEmployeeData(employeeData)
    }

    fun submitEmployeeDataList(employeeDataList: EmployeeDataList?) {
        submitList(employeeDataList?.employees)
    }
}

// Helper class for the ListAdapter
private class EmployeeDiffCallback : DiffUtil.ItemCallback<EmployeeData>() {
    override fun areItemsTheSame(oldItem: EmployeeData, newItem: EmployeeData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: EmployeeData, newItem: EmployeeData): Boolean {
        return oldItem == newItem
    }
}
