# SFDXお試し

SFDXによるソースコードベースの開発を目的として検証

## 0. アジェンダ

* [1. 導入](#1-導入)
* [2. 初期設定手順](#2-初期設定手順)
* [3. APPENDIX](#3-appendix)
* [4. Tips](#4-tips)

## 1. 導入

### 1-1. SFDX化によるメリット

* 各環境間での想定外の差分がシステム的に担保
  * テストしたソース = 本番で動くソース として担保
* メンバー間での変更の競合を検知できる
* リリースフローが統一されるため、リリース毎に手順書を作成する必要がなくなる
  * CI/CDなどで、リリース自動化することでソースを担保可能
* 変更箇所や経緯を履歴と紐づけて管理・集約可能

## 2. 初期設定手順

|No.|項目||①初期SFDX設定時|②SFDX設定済みのGitクローン時|③ローカルにプロジェクト作成後|
|---|---|---|:---:|:---:|:---:|
|2-0|[環境](#2-0-環境)||●|●|●|
|2-1|[DevHubの有効化](#2-1-DevHubの有効化-初回のみ)||●|||
|2-2|[ローカル環境にプロジェクトを作成](#2-2-ローカル環境にプロジェクトを作成)||●|● ※③||
|2-3|[スクラッチ組織の起動〜変更](#2-3-スクラッチ組織の起動変更)||●|●|●|
|2-4|[ソースへの反映](#2-4-ソースへの反映)||●|●|●|

### 2-0. 環境

* 本番組織
  * Developer Edition ※サンプルプロジェクトのため
* salesforce CLI
  * sfdx-cli/7.102.0 darwin-x64 node-v16.1.0
* Git
  * git version 2.30.1 (Apple Git-130)
* VScode
  * Version: 1.56.2 (Universal)
* Gitlab

### 2-1. DevHubの有効化 (初回のみ)

スクラッチ組織を有効化するためにDevHubの設定を更新

![DevHub設定](./assets/setting_for_devhub_org.PNG)

### 2-2. ローカル環境にプロジェクトを作成

__①新規プロジェクトに導入する場合：__

* 下記のコマンドより、SFDXプロジェクトを作成

  ```bash
  sfdx force:project:create -n [任意プロェクト名]
  ```

  * 任意のプロジェクト名: プロジェクトフォルダ名として作成されます

__②既存プロジェクトに導入する場合：__

* [こちら](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_ws_create_from_existing.htm)のドキュメントを参考に、既存プロジェクトのソースをローカルへ反映

__③既にGit管理しているプロジェクトがある場合：__

* Gitリポジトリより、ソースをクローンしローカルに反映

  ```bash
  git clone [任意のSFDX管理プロジェクト]
  ```

### 2-3. スクラッチ組織の起動〜変更

1. DevHubを設定した組織へログイン

    以下のコマンドを実行し、起動したブラウザタブよりDevHubを設定した組織へログイン

    ```bash
    sfdx force:auth:web:login -d -a [任意の環境名]
    ```

    * 任意の環境名: ログインした情報をエイリアスとして保持するために任意で指定

1. スクラッチ組織を作成

    ```bash
    sfdx force:org:create -s -f config/project-scratch-def.json -a [任意のスクラッチ組織名]
    ```

    * 任意のスクラッチ組織名
      * 作成しスクラッチ組織名をエイリアスとして保持するために任意で指定
<br>
1. ローカルのソースを反映

    ```bash
    sfdx force:source:push
    ```

    * スクラッチ組織が複数ある場合には、
<br>
1. スクラッチ組織を起動

    ```bash
    sfdx force:org:open -u [スクラッチ組織名]
    ```

    * 1.で指定したスクラッチ組織名を指定してログイン
<br>
1. 通常と同じ手順でUIより設定を変更

### 2-4. ソースへの反映

1. UIからの設定変更後、下記のコマンドでローカルのソースへ反映

    ```bash
    sfdx force:source:pull
    ```

1. ローカルに反映された変更をコミット

    ```bash
    > git commit
    ```

1. コミットをリモートリポジトリへ反映

    ```bash
    > git push origin [任意のブランチ]
    ```

## 3. APPENDIX

### 3-1. 開発・運用フロー案

### 3-2. CI/CDフロー案

Sandbox/本番環境へのリリースをCI/CD化する際の処理イメージ

1. リリース環境(Sandbox/本番環境)へログイン

    ```bash
    export CONSUMER_KEY=[事前に作成してコピーしたコンシューマーキー]
    export JWT_KEY_FILE=[server.keyのパス]
    export HUB_USERNAME=[DevHubユーザー名]

    sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${HUB_USERNAME} -f ${JWT_KEY_FILE} -a jwt
    ```

    * TBU :

1. VCSからソースコードをローカルにクローン/プル

  ```bash
  > 
  ```

③ソース形式をメタデータ形式に変換する
sfdx force:source:convert -d <ディレクトリ名>

③Sandbox/本番環境へリリース

  ```bash
  > sfdx force:mdapi:deploy -d <ディレクトリ名> -u <リリース環境(Sandbox/本番環境)の別名>
  ```

### 3-3. 参考文献

* [Qiita記事](https://qiita.com/yhayashi30/items/80dd868f2e15aac67072)
* [メタデータに含まれない変更](https://developer.salesforce.com/docs/atlas.ja-jp.api_meta.meta/api_meta/meta_unsupported_types.htm)

## 4. Tips

### 4-1. スクラッチ組織の作成ができない。。。 (1)

```bash
# error message
ERROR running force:org:create:  This command requires a dev hub org username set either with a flag or by default in the config.
```

<details>
<summary>解決方法</summary>

* ログイン情報が正しくない可能性があります。再度下記のコマンドよりログインしてください。

  ```bash
  > sfdx force:auth:web:login -d -a [任意の環境名]
  ```

</details>

### 4-2. スクラッチ組織の作成ができない。。。 (2)

```bash
# error message
ERROR running force:org:create:  この組織は有効なスクラッチ組織の制限に達したため、サインアップ要求に失敗しました
```

<details>
<summary>解決方法</summary>

* スクラッチ組織が作成上限に達していると思われます。DevHub設定をした組織より、不要なスクラッチ組織を削除ください。
  * 手順
    1. DevHub組織を設定した環境へUIでログインします。
    2. アプリケーションランチャーから`有効なDevHub組織`を検索します。
    3. レコードの件数がスクラッチ組織を作成可能な上限に達している場合、不要と思われる組織を削除します。
        * [Edition毎のスクラッチ組織最大数](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_scratch_orgs_editions_and_allocations.htm)
        * 下記のコマンドで現在の使用状況を確認可能です。

          ```bash
          > sfdx force:limits:api:display -u [設定したDevHubのエイリアス名]
          ```

</details>
