package com.example.braviaremotecontroler.ui.keyboard

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.braviaremotecontroler.R
import com.example.braviaremotecontroler.api.BraviaRemoteManager
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

        // 十字キーのセットアップ (RemoteControlFragmentと同じスタイル)
        binding.btnUp.setOnClickListener { sendCommand(it) { m -> m.up() } }
        binding.btnDown.setOnClickListener { sendCommand(it) { m -> m.down() } }
        binding.btnLeft.setOnClickListener { sendCommand(it) { m -> m.left() } }
        binding.btnRight.setOnClickListener { sendCommand(it) { m -> m.right() } }
        binding.btnConfirm.setOnClickListener { sendCommand(it) { m -> m.confirm() } }

        // Back, Mute, Volume Controls
        binding.btnBack.setOnClickListener { sendCommand(it) { m -> m.back() } }
        binding.btnMute.setOnClickListener { sendCommand(it) { m -> m.mute() } }
        binding.btnVolUp.setOnClickListener { sendCommand(it) { m -> m.volumeUp() } }
        binding.btnVolDown.setOnClickListener { sendCommand(it) { m -> m.volumeDown() } }

        viewModel.errorEvent.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    /**
     * 触覚フィードバックを実行し、ViewModelにコマンド送信を依頼します。
     */
    private fun sendCommand(view: View, action: suspend (BraviaRemoteManager) -> Unit) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        viewModel.sendCommand(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
