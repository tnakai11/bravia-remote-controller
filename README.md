# Bravia Remote Controller

Sony Bravia TV をネットワーク経由で操作するための Android リモコンアプリです。

## 主な機能

- **基本操作**: 電源、入力切換、音量調整、消音、チャンネル切換。
- **ナビゲーション**: 十字キー（DPAD）、決定、ホーム、戻るボタン。
- **アプリ起動**: YouTube や Netflix などのアプリをワンタッチで起動（TV側が対応している場合）。
- **カスタマイズ**: テレビの IP アドレスと Pre-Shared Key (PSK) を設定画面から簡単に変更可能。
- **デバッグ機能**: TV から取得した生の IRCC コマンドを一覧表示し、直接送信してテストすることが可能。

## 技術スタック

- **Language**: Kotlin
- **Architecture**: MVVM (ViewModel, LiveData/StateFlow)
- **Networking**: OkHttp (JSON-RPC / IRCC over HTTP)
- **Navigation**: Jetpack Navigation Component
- **UI**: Material Design, XML Layouts

## セットアップ

1. **TV 側の設定**:
   - ブラビアの「設定」>「ネットワーク」>「ホームネットワーク設定」>「 IP コントロール」を開きます。
   - 「認証」を「ノーマルおよび事前共有鍵」に設定します。
   - 「事前共有鍵」に適当な文字列（例: `1234`）を入力します。
2. **アプリの設定**:
   - アプリを起動し、設定画面から TV の IP アドレスと、上記で設定した事前共有鍵（PSK）を入力します。

## 開発環境

- Android Studio Koala 以降推奨
- Gradle (Kotlin DSL)
