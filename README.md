# Codic NetBeans Plugin

エディタで [codic](https://codic.jp/) のネーミング生成機能を利用するためのNetBeansのプラグインです。

## 環境

- NetBeans 8.1+

## インストール

[releases](https://github.com/codic-project/codic-netbeans-plugin/releases)からnbmをダウンロードします。
ツール > プラグイン > ダウンロード済 > プラグインの追加 からダウンロードしたnbmを選択してインストールします。

## 使い方

#### キーマップの設定

ツール > オプション > キーマップ で`codic-translate` のアクションに適当なショートカットを設定してください。

#### Codicの設定

ツール > オプション > その他 > Codic で以下のオプションを設定してください。
プロジェクト毎に設定したい場合は、プロジェクト・プロパティ > Codicで同様の設定をすることができます。

- Access Token(必須:ログインした状態でAPIステータスページから取得)
- Project(必須)
- Casing

**注意1:** Casingの指定がない場合の動作は以下の通りです。

- スペース区切りのネーミングを取得します。
- さらに、内部で他のケースにも変換したものを候補として表示します。

**注意2:** プロジェクト・プロパティで「グローバルオプションを使う」を選択して設定を有効にする場合は、保存されているAccess Tokenは初期化されます。

**注意3:** 一部のプロジェクト(e.g. C/C++)では、プロジェクトの設定を使用することができません。


#### ネーミング生成

キーマップの設定で指定したショートカットを実行すると、ネーミング生成用のダイアログが表示されます。
テキストフィールドがフォーカスされている時の各キーの動作は次の通りです。

|Key                                |動作                                              |
|:----------------------------------|:-------------------------------------------------|
|<kbd>Enter</kbd>                   |選択されている候補を挿入する                      |
|<kbd>Shift</kbd> + <kbd>Enter</kbd>|ネーミングを取得する                              |
|<kbd>Shift</kbd> + <kbd>↑</kbd>   |選択されているケースを1つ上のものに変更する        |
|<kbd>Shift</kbd> + <kbd>↓</kbd>   |選択されているケースを1つ下のものに変更する        |
|<kbd>↑</kbd>                      |選択されているネーミング候補を1つ上のものに変更する|
|<kbd>↓</kbd>                      |選択されているネーミング候補を1つ下のものに変更する|

変換したい日本語をエディタ内で選択してから実行することも可能です。

## バグ・リクエスト

バグやリクエストがありましたら、GitHubの [Issues](https://github.com/codic-project/codic-netbeans-plugin/issues) へ報告をおねがいします。

## ライセンス

The MIT license
