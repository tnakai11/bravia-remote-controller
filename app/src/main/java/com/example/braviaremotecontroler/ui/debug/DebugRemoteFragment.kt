package com.example.braviaremotecontroler.ui.debug

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.braviaremotecontroler.R
import com.example.braviaremotecontroler.databinding.FragmentDebugRemoteBinding

class DebugRemoteFragment : Fragment() {

    private var _binding: FragmentDebugRemoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DebugRemoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebugRemoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DebugCommandAdapter { view, code ->
            if (binding.switchLock.isChecked) {
                Toast.makeText(context, "ロックを解除してください", Toast.LENGTH_SHORT).show()
                return@DebugCommandAdapter
            }
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            viewModel.sendCommand(code)
        }
        binding.recyclerView.adapter = adapter

        viewModel.commands.observe(viewLifecycleOwner) { commands ->
            adapter.submitList(commands.toList().sortedBy { it.first })
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.loadCommands()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DebugCommandAdapter(private val onClick: (View, String) -> Unit) :
        RecyclerView.Adapter<DebugCommandAdapter.ViewHolder>() {

        private var items: List<Pair<String, String>> = emptyList()

        fun submitList(newList: List<Pair<String, String>>) {
            items = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_debug_command, parent, false)
            return ViewHolder(view as Button)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (name, code) = items[position]
            holder.button.text = name
            holder.button.setOnClickListener { onClick(it, code) }
        }

        override fun getItemCount() = items.size

        class ViewHolder(val button: Button) : RecyclerView.ViewHolder(button)
    }
}
