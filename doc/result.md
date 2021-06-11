# SFDXお試し

SFDXによるソースコードベースの開発を目的として検証

## 0. アジェンダ

* [1. 導入](#1-導入)
* [2. 初期設定手順](#2-初期設定手順)
* [3. APPENDIX](#3-appendix)
* [4. Tips](#4-tips)

## 1. 導入

### 1-1. SFDX化によるメリット

SFDX化によるソース駆動型の開発により、バージョン管理システムなどを採用でき、下記の様なメリットが得られる見込み。

* Salesforce開発・運用プロセスの効率化
  * 手作業によるリリースプロセスを自動化可能にする
* 改修差分と環境差分の管理
  * 改修差分や履歴を追跡可能にし、環境の差異も可視化可能にする
* ソースの完全性担保
  * 各環境間のリソースが一致することをシステム的に担保可能にする

## 2. 初期設定手順

|No.|項目||①初期SFDX設定時|②SFDX設定済み + ソースがGit管理されている場合|③ローカルにプロジェクト作成後|
|---|---|---|:---:|:---:|:---:|
|2-0|[環境](#2-0-環境)||●|●|●|
|2-1|[DevHubの有効化](#2-1-DevHubの有効化-初回のみ)||●|||
|2-2|[ローカル環境にプロジェクトを作成](#2-2-ローカル環境にプロジェクトを作成)||●|● ※③||
|2-3|[スクラッチ組織の起動〜変更](#2-3-スクラッチ組織の起動変更)||●|●|●|
|2-4|[ソースへの反映](#2-4-ソースへの反映)||●|●|●|

### 2-0. 環境

本検証時に使用した環境情報

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

* [こちら](./cicd-varification.md)を参照ください

### 3-3. 参考文献

* [Qiita記事](https://qiita.com/yhayashi30/items/80dd868f2e15aac67072)
* [メタデータに含まれない変更](https://developer.salesforce.com/docs/atlas.ja-jp.api_meta.meta/api_meta/meta_unsupported_types.htm)

### 3-4. 課題

* デプロイの方法について、ソース形式とメタデータ形式どちらを採用するか。
  * ソース方式のリリースはバグが残存、メタデータ形式の開発はバージョン管理が困難なため、ソース方式で開発しリリース時にメタデータ形式に変換してリリースする方法を検討。
  * [参考](https://scrapbox.io/nesiyama/%5BSalesforce_CLI%5D_force:source_%E3%81%A8_force:mdapi_%E3%81%A3%E3%81%A6%E3%81%A9%E3%81%86%E9%81%95%E3%81%86%E3%81%AE%EF%BC%9F)

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


## Demo

### 1. SFDXの紹介

* [参考リンク](https://buildersbox.corp-sansan.com/entry/2019/07/25/125610)

### 2. スクラッチ組織のデモ

* 本番組織へDevHub組織を設定
  * 設定　⇨　DevHub
* JWTキーの設定
  * 設定　⇨　新規接続アプリケーション
* ローカルでCLIログイン

    ```bash
    export CONSUMER_KEY=3MVG95mg0lk4batgzm6iNzpzgAZC0Vi6_8Ss60MiSnjWKMNUIdykjVDmagvaVhvfmBNAQGw.0McuNUvUrp2_g
    export HUB_USERNAME=jenkins@service.dev.com
    export JWT_KEY_FILE=../certifications/server.key
    sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${HUB_USERNAME} -f ${JWT_KEY_FILE} -a jwt
    ```

* スクラッチ組織の作成

    ```bash
    sfdx force:org:create -s -f config/project-scratch-def.json -a demo
    ```

* スクラッチ組織の起動

    ```bash
    sfdx force:org:open -u demo
    ```

  * 生成されたURLにアクセスでスクラッチ組織環境への接続が完了
  * 起動時はデフォルト設定となっていることを確認

* スクラッチ組織へローカルのソースを反映
  
    ```bash
    sfdx force:source:push
    ```

  * ローカルの変更が反映されていることを確認
    * 今回はケースオブジェクトへtestのカスタム項目追加

* スクラッチ組織へ変更し、ローカルのメタデータへ変更を反映
  * 設定を変更 (カスタム項目を追加し、ページレイアウトを編集など)

    ```bash
    sfdx force:source:pull
    ```

  * メタデータに変更が反映されていることを確認

* Gitへ反映
  
  ```bash
  git add *
  git commit -m "add demo edit"
  git push origin HEAD
  ```

### 3. Jenkins CI/CDの確認

* CI/CDソースの確認
  * [ファイル格納先](https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample/-/blob/develop/jenkins/jenkins-sample/jenkinsfiles/sfdx-sample.groovy)

* Jenkinsの起動とアクセス

  ```bash
  cd ./jenkins-sample
  docker-compose up
  ```

  * http://localhost:18080/ へアクセス

* sfdx-sampleで、Pipelineを実行

* 本番組織へ変更が反映されていることを確認
