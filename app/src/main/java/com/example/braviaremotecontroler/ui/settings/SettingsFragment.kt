package com.example.braviaremotecontroler.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.braviaremotecontroler.R
import com.example.braviaremotecontroler.databinding.FragmentSettingsBinding

/**
 * アプリの設定（IPアドレス、PSK）を表示・編集するためのフラグメント。
 * 設定の保存機能および接続テスト機能を提供します。
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupListeners()
    }

    /**
     * ViewModelのLiveDataを購読し、UIを更新します。
     */
    private fun setupObservers() {
        viewModel.ipAddress.observe(viewLifecycleOwner) { ip ->
            if (binding.editIpAddress.text.toString() != ip) {
                binding.editIpAddress.setText(ip)
            }
        }

        viewModel.psk.observe(viewLifecycleOwner) { psk ->
            if (binding.editPsk.text.toString() != psk) {
                binding.editPsk.setText(psk)
            }
        }

        viewModel.isTesting.observe(viewLifecycleOwner) { isTesting ->
            binding.progressTest.visibility = if (isTesting) View.VISIBLE else View.GONE
            binding.btnTestConnection.isEnabled = !isTesting
            if (isTesting) {
                binding.textTestResult.visibility = View.GONE
            }
        }

        viewModel.testResult.observe(viewLifecycleOwner) { success ->
            if (success == null) return@observe

            binding.textTestResult.visibility = View.VISIBLE
            if (success) {
                binding.textTestResult.setText(R.string.test_connection_success)
                binding.textTestResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_green))
            } else {
                binding.textTestResult.setText(R.string.test_connection_failed)
                binding.textTestResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red))
            }
        }
    }

    /**
     * ボタンのクリックリスナーなどをセットアップします。
     */
    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val ip = binding.editIpAddress.text.toString()
            val psk = binding.editPsk.text.toString()

            val success = viewModel.saveSettings(ip, psk)
            if (success) {
                Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.settings_error_fill_all, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTestConnection.setOnClickListener {
            val ip = binding.editIpAddress.text.toString()
            val psk = binding.editPsk.text.toString()

            if (ip.isEmpty() || psk.isEmpty()) {
                Toast.makeText(context, R.string.settings_error_test_fill, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.testConnection(ip, psk)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
