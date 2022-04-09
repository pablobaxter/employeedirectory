package com.frybits.squarechallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.frybits.squarechallenge.databinding.FragmentEmployeeDirectoryBinding
import com.frybits.squarechallenge.utils.EmployeeAdapter
import com.frybits.squarechallenge.viewmodels.EmployeeDirectoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmployeeDirectoryFragment : Fragment() {

    private val employeeDirectoryViewModel by viewModels<EmployeeDirectoryViewModel>()

    private var swipeToRefreshJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEmployeeDirectoryBinding.inflate(inflater, container, false)

        // Hold local reference to the adapter
        val adapter = EmployeeAdapter(lifecycleScope, employeeDirectoryViewModel)

        // Set layout based on screen width in dp
        binding.employeeRecyclerView.layoutManager = GridLayoutManager(context, resources.configuration.screenWidthDp / resources.getInteger(R.integer.min_cardview_width_dp))
        binding.employeeRecyclerView.adapter = adapter

        binding.recyclerViewSwipeRefresh.setOnRefreshListener {
            swipeToRefreshJob?.cancel() // If previous refresh job is running still, cancel this one.
            lifecycleScope.launch {
                binding.clearMessage()
                try {
                    binding.loadDataInto(
                        adapter = adapter,
                        refresh = true
                    )
                } finally { // Always ensure refresh progress bar is cleared
                    binding.recyclerViewSwipeRefresh.isRefreshing = false
                }
            }
        }

        // Pre-load recycler view
        lifecycleScope.launch {
            binding.clearMessage()
            try {
                binding.employeeProgressBar.visibility = View.VISIBLE
                binding.loadDataInto(
                    adapter = adapter,
                    refresh = false
                )
            } finally { // Always ensure progress bar is cleared
                binding.employeeProgressBar.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onLowMemory() {
        super.onLowMemory()
        employeeDirectoryViewModel.onLowMemory()
    }

    // Helper extension functions
    private fun FragmentEmployeeDirectoryBinding.clearMessage() {
        messageTextView.text = ""
        messageTextView.visibility = View.GONE
    }

    private suspend fun FragmentEmployeeDirectoryBinding.loadDataInto(
        adapter: EmployeeAdapter,
        refresh: Boolean
    ) {
        employeeDirectoryViewModel.getEmployees(refresh).onFailure {
            messageTextView.visibility = View.VISIBLE
            messageTextView.setText(R.string.error_message)
        }.onSuccess {
            adapter.submitEmployeeDataList(it)
            if (adapter.currentList.isEmpty()) {
                messageTextView.visibility = View.VISIBLE
                messageTextView.setText(R.string.empty_message)
            }
        }
    }
}
