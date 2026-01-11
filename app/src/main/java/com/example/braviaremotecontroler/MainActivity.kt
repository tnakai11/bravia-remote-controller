package com.example.braviaremotecontroler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.braviaremotecontroler.databinding.ActivityMainBinding

/**
 * アプリケーションのメインアクティビティです。
 * ナビゲーションコンポーネントを使用して、リモコン操作画面と設定画面の遷移を管理します。
 * 画面構成に応じて、ナビゲーションドロワーまたはボトムナビゲーションをセットアップします。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        setupNavigation(navController)
    }

    /**
     * デバイスの画面構成（ドロワー表示またはボトムナビ表示）に合わせてナビゲーションをセットアップします。
     */
    private fun setupNavigation(navController: NavController) {
        val topLevelDestinations = setOf(
            R.id.nav_remote_control,
            R.id.nav_debug_remote,
            R.id.nav_settings
        )

        // ナビゲーションドロワー（主にタブレットやワイド画面、またはサイドメニュー構成）
        binding.navView?.let { navView ->
            appBarConfiguration = AppBarConfiguration(topLevelDestinations, binding.drawerLayout)
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

        // ボトムナビゲーション（主にスマートフォン）
        binding.appBarMain.contentMain.bottomNavView?.let { bottomNav ->
            appBarConfiguration = AppBarConfiguration(topLevelDestinations)
            setupActionBarWithNavController(navController, appBarConfiguration)
            bottomNav.setupWithNavController(navController)
        }
    }

    /**
     * アクションバーの「戻る」ボタン（Upボタン）が押された際の処理を制御します。
     */
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        return navHostFragment.navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
