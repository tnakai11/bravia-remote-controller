package com.example.braviaremotecontroler.ui

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.braviaremotecontroler.api.BraviaRemoteManager
import com.example.braviaremotecontroler.databinding.FragmentRemoteControlBinding

/**
 * テレビのリモコン操作を行うためのフラグメント。
 * 各ボタンのクリックイベントを処理し、ViewModelを介してコマンドを送信します。
 */
class RemoteControlFragment : Fragment() {

    private var _binding: FragmentRemoteControlBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RemoteControlViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemoteControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // エラーメッセージの購読
        viewModel.errorEvent.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // 画面が表示されるたびに最新の設定を反映
        viewModel.updateRemoteManager()

        // アプリ起動 (動的解決コマンド)
        binding.btnYoutube.setOnClickListener { sendCommand(it) { m -> m.youtube() } }
        binding.btnNetflix.setOnClickListener { sendCommand(it) { m -> m.netflix() } }
        binding.btnTv.setOnClickListener { sendCommand(it) { m -> m.tv() } }
        binding.btnDemoMode.setOnClickListener { sendCommand(it) { m -> m.demoMode() } }

        // 電源・入力
        binding.btnPower.setOnClickListener { sendCommand(it) { m -> m.power() } }
        binding.btnInput.setOnClickListener { sendCommand(it) { m -> m.input() } }

        // ナビゲーション (D-Pad)
        binding.btnUp.setOnClickListener { sendCommand(it) { m -> m.up() } }
        binding.btnDown.setOnClickListener { sendCommand(it) { m -> m.down() } }
        binding.btnLeft.setOnClickListener { sendCommand(it) { m -> m.left() } }
        binding.btnRight.setOnClickListener { sendCommand(it) { m -> m.right() } }
        binding.btnConfirm.setOnClickListener { sendCommand(it) { m -> m.confirm() } }

        // Home & Back
        binding.btnHome.setOnClickListener { sendCommand(it) { m -> m.home() } }
        binding.btnBack.setOnClickListener { sendCommand(it) { m -> m.back() } }

        // 音量操作
        binding.btnVolUp.setOnClickListener { sendCommand(it) { m -> m.volumeUp() } }
        binding.btnVolDown.setOnClickListener { sendCommand(it) { m -> m.volumeDown() } }
        binding.btnMute.setOnClickListener { sendCommand(it) { m -> m.mute() } }

        // チャンネル操作
        binding.btnChUp.setOnClickListener { sendCommand(it) { m -> m.channelUp() } }
        binding.btnChDown.setOnClickListener { sendCommand(it) { m -> m.channelDown() } }
    }

    /**
     * 触覚フィードバックを実行し、ViewModelにコマンド送信を依頼します。
     *
     * @param view クリックされたビュー。
     * @param action [BraviaRemoteManager] を使用して実行するアクション。
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
