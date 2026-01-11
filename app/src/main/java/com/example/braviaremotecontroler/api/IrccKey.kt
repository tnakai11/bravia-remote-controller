package com.example.braviaremotecontroler.api

/**
 * Sony Bravia TVを制御するためのIRCC (Infrared Remote Control Command) キーコードの列挙型。
 * 各列挙子は、テレビに送信される特定の操作に対応するBase64エンコードされたコードを保持します。
 *
 * @property code テレビに送信するIRCCコード（Base64形式）。
 */
enum class IrccKey(val code: String) {
    /** 電源 */
    Power("AAAAAQAAAAEAAAAVAw=="),
    /** 入力切換 */
    Input("AAAAAQAAAAEAAAAlAw=="),
    /** Syncメニュー */
    SyncMenu("AAAAAgAAABoAAABYAw=="),
    /** HDMI 1 */
    Hdmi1("AAAAAgAAABoAAABaAw=="),
    /** HDMI 2 */
    Hdmi2("AAAAAgAAABoAAABbAw=="),
    /** HDMI 3 */
    Hdmi3("AAAAAgAAABoAAABcAw=="),
    /** HDMI 4 */
    Hdmi4("AAAAAgAAABoAAABdAw=="),

    /** 数字 1 */
    Num1("AAAAAQAAAAEAAAAAAw=="),
    /** 数字 2 */
    Num2("AAAAAQAAAAEAAAABAw=="),
    /** 数字 3 */
    Num3("AAAAAQAAAAEAAAACAw=="),
    /** 数字 4 */
    Num4("AAAAAQAAAAEAAAADAw=="),
    /** 数字 5 */
    Num5("AAAAAQAAAAEAAAAEAw=="),
    /** 数字 6 */
    Num6("AAAAAQAAAAEAAAAFAw=="),
    /** 数字 7 */
    Num7("AAAAAQAAAAEAAAAGAw=="),
    /** 数字 8 */
    Num8("AAAAAQAAAAEAAAAHAw=="),
    /** 数字 9 */
    Num9("AAAAAQAAAAEAAAAIAw=="),
    /** 数字 0 */
    Num0("AAAAAQAAAAEAAAAJAw=="),

    /** ドット (.) */
    Dot("AAAAAgAAAJcAAAAdAw=="),
    /** 字幕 (CC) */
    CC("AAAAAgAAAJcAAAAoAw=="),
    /** 赤ボタン */
    Red("AAAAAgAAAJcAAAAlAw=="),
    /** 緑ボタン */
    Green("AAAAAgAAAJcAAAAmAw=="),
    /** 黄ボタン */
    Yellow("AAAAAgAAAJcAAAAnAw=="),
    /** 青ボタン */
    Blue("AAAAAgAAAJcAAAAkAw=="),

    /** 上 */
    Up("AAAAAQAAAAEAAAB0Aw=="),
    /** 下 */
    Down("AAAAAQAAAAEAAAB1Aw=="),
    /** 右 */
    Right("AAAAAQAAAAEAAAAzAw=="),
    /** 左 */
    Left("AAAAAQAAAAEAAAA0Aw=="),
    /** 決定 */
    Confirm("AAAAAQAAAAEAAABlAw=="),
    /** ヘルプ */
    Help("AAAAAgAAAMQAAABNAw=="),
    /** 画面表示 */
    Display("AAAAAQAAAAEAAAA6Aw=="),
    /** オプション */
    Options("AAAAAgAAAJcAAAA2Aw=="),
    /** 戻る */
    Back("AAAAAgAAAJcAAAAjAw=="),
    /** ホーム */
    Home("AAAAAQAAAAEAAABgAw=="),

    /** 音量＋ */
    VolumeUp("AAAAAQAAAAEAAAASAw=="),
    /** 音量－ */
    VolumeDown("AAAAAQAAAAEAAAATAw=="),
    /** 消音 */
    Mute("AAAAAQAAAAEAAAAUAw=="),
    /** 音声切換 */
    Audio("AAAAAQAAAAEAAAAXAw=="),

    /** チャンネル＋ */
    ChannelUp("AAAAAQAAAAEAAAAQAw=="),
    /** チャンネル－ */
    ChannelDown("AAAAAQAAAAEAAAARAw=="),

    /** 再生 */
    Play("AAAAAgAAAJcAAAAaAw=="),
    /** 一時停止 */
    Pause("AAAAAgAAAJcAAAAZAw=="),
    /** 停止 */
    Stop("AAAAAgAAAJcAAAAYAw=="),
    /** 早送り */
    FlashPlus("AAAAAgAAAJcAAAB4Aw=="),
    /** 巻き戻し */
    FlashMinus("AAAAAgAAAJcAAAB5Aw=="),
    /** 前へ */
    Prev("AAAAAgAAAJcAAAA8Aw=="),
    /** 次へ */
    Next("AAAAAgAAAJcAAAA9Aw=="),
}
