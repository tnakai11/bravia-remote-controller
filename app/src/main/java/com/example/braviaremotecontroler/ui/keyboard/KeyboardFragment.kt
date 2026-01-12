package com.example.braviaremotecontroler.ui.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.braviaremotecontroler.R
import com.example.braviaremotecontroler.databinding.FragmentKeyboardBinding
import com.example.braviaremotecontroler.ui.RemoteControlViewModel

class KeyboardFragment : Fragment() {

    private var _binding: FragmentKeyboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RemoteControlViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeyboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 標準のテキスト送信 (setTextForm)
        binding.buttonSend.setOnClickListener {
            val text = binding.editText.text.toString()
            if (text.isNotEmpty()) {
                viewModel.sendCommand { manager ->
                    val success = manager.setTextForm(text)
                    activity?.runOnUiThread {
                        if (success) {
                            Toast.makeText(context, R.string.text_sent_success, Toast.LENGTH_SHORT)
                                .show()
                            binding.editText.text?.clear()
                        } else {
                            Toast.makeText(context, R.string.text_sent_failed, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        // YouTube向けのカーソル入力
        binding.buttonYouTubeSearch.setOnClickListener {
            val text = binding.editText.text.toString()
            if (text.isNotEmpty()) {
                viewModel.sendCommand { manager ->
                    manager.typeTextViaCursor(text)
                    activity?.runOnUiThread {
                        Toast.makeText(context, R.string.youtube_typing_started, Toast.LENGTH_SHORT).show()
                        binding.editText.text?.clear()
                    }
                }
            }
        }

        // 十字キーのセットアップ
        binding.btnUp.setOnClickListener { viewModel.sendCommand { it.up() } }
        binding.btnDown.setOnClickListener { viewModel.sendCommand { it.down() } }
        binding.btnLeft.setOnClickListener { viewModel.sendCommand { it.left() } }
        binding.btnRight.setOnClickListener { viewModel.sendCommand { it.right() } }
        binding.btnConfirm.setOnClickListener { viewModel.sendCommand { it.confirm() } }

        viewModel.errorEvent.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
