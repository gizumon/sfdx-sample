# テスト自動化検証

Salesforceに適用可能なテスト自動化ツールを検証する目的

## 0. アジェンダ

- [テスト自動化検証](#テスト自動化検証)
  - [0. アジェンダ](#0-アジェンダ)
  - [1. Salesforce標準Apexテスト](#1-salesforce標準apexテスト)
    - [1-1. ApexTestの実行](#1-1-apextestの実行)
  - [2. Salesforceの静的解析](#2-salesforceの静的解析)
  - [3. Sonar cubeの設定・起動](#3-sonar-cubeの設定起動)
    - [3-1. Code scanインストール](#3-1-code-scanインストール)
    - [3-2. Code scanの実行](#3-2-code-scanの実行)
    - [3-3. Jenkinsとの連携](#3-3-jenkinsとの連携)
  - [X. リファレンス](#x-リファレンス)

## 1. Salesforce標準Apexテスト

Apexのテストクラス実行を調査

### 1-1. ApexTestの実行

CI/CDに適用可能なテストコマンドは下記。

```bash
sfdx force:mdapi:deploy -w '-1' -d ./release -u sfdx --testlevel=RunLocalTests
```

* --testlevel : RunLocalTest指定でリリース時にApexテストを実行。
  * テストが失敗した場合は、リリースが失敗
    ![test-failed-capture](./assets/test-failed-capture.png)

<br>

<details><summary>備考：</summary>

下記の通り、テストコマンドが存在するが、CI/CDには適用難しい。

```bash
sfdx force:apex:test:run --synchronous -w -1 -c -v -r human --testlevel=RunLocalTests -u [ユーザー]
```

* 環境上でテスト実行するため、リリース前のモジュールに対するテストは不可。
  * 従って、CI/CD時のテストとしては不適。各自がスクラッチ組織上でテスト書いた際に実行するコマンドとして運用推奨。
* スクラッチ組織を利用して、単体テスト実行環境を作成する方法もあるが、スクラッチ組織に作成上限がある関係から、適用は現実的でない認識。

</details>

<br>

## 2. Salesforceの静的解析

* Sonar Cubeとの親和性の観点から、[code.scan](https://www.codescan.io/)をメインで調査
  * Cloud版とSelf-Hosted版があり、Self-Hosted版がSonarQubeのプラグインとして導入可能なバージョン

その他

* SonarQubeのプラグインとして、利用可能な[Salesforce PDM](https://github.com/SalesforceFoundation/salesforce-sonar-plugin)も利用可能
  * 上記のURLよりクローンしてきたソースをmvnでビルドし、プラグインを生成
  * extensions配下に配置して、起動でプラグインは起動
  * ※指摘やレポートを見るまでの動作は未検証

## 3. Sonar cubeの設定・起動

SonarQubeに適用可能なプラグインで調査


* docker-composeにsonar-cubeを追加。
  * [docker-compose.yml](../jenkins-sample/docker-compose.yml)
* 下記で起動してログイン
  
  ```bash
  docker-compose up -d
  ```

  * ユーザーはデフォルトで存在する以下を使用
    * user: admin, pass: admin

|key|value|備考|
|---|---|---|
|project key|sfdx-sample||
|token(name)|sfdx-sample-codescan||
|token(val)|[XXX](../jenkins-sample/certifications/memo.md)|※ドキュメント参照|

* Sonar Scannerの導入
  * 検証用でローカルに導入 ([参考](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/))
  * プロジェクトのルートにsonar-project.propertiesを追加
  * sfdxのプラグインを利用する場合は、インストール不要

### 3-1. Code scanインストール

__①SonarQubeのプラグインインストール__

* SonarQubeコンテナ内のpluginsディレクトリ内にjarファイルを配置
  * /opt/sonarqube/extensions/plugins/sonar-salesforce-plugin-4.5.6.jar
  * /opt/sonarqube/extensions/plugins/sonar-codescanlang-plugin-4.5.6.jar
* コンテナを再起動
* Administration > Configuration > CodeScanが表示されていればインストール完了
  * CodeScanの利用には、[ライセンスの申請と設定](https://docs.codescan.io/hc/en-us/articles/360011885512-Installing-CodeScan-Self-Hosted)が必要

__②SFDXのプラグインインストール__

下記のコマンドより、sfdx経由でCodeScanを実行可能なプラグインが入手可能

```bash
sfdx plugins:install sfdx-codescan-plugin
```

### 3-2. Code scanの実行

CodeScanの実行には以下の設定が必要です。

* プロジェクトの作成
  * Projectsタブ > Create new project
  * 下記で作成します
    * Project key: sfdx-sample
    * Display name: sfdx-sample
    * Generate a token: sfdx-sample-codescan
  * 生成されたトークン情報はクライアント側で使用します

* CodeScanのライセンスを更新します
  * [Administration > CodeScan](http://localhost:9000/admin/settings?category=codescan) にアクセスします
  * CodeScan license欄を以下に更新します
    * [こちらのCodeScanライセンスを参照](../jenkins-sample/certifications/memo.md#codescan)

* クライアント側でCodeScan(SonarQube)を実行

  ```bash
  sonar-scanner \
    -Dsonar.projectKey=[プロジェクトキー] \
    -Dsonar.sources=[Sourceディレクトリ] \
    -Dsonar.host.url=[SonarQube動作環境URL] \
    -Dsonar.login=[プロジェクトアクセストークン]
  ```

### 3-3. Jenkinsとの連携

* Jenkins側でsfdx-poc-sampleのジョブを使用します。([こちら](./cicd-varification.md#1-6-ジョブ設定)で作成)
  * パラメータ付きビルドを選択 ([URL]( http://localhost:18080/job/sfdx-poc-sample/build?delay=0sec))
  * 下記のパラメータを変更して実行
    * IS_RUN_STATIC_ANALYSIS: TRUE
    * SONARQUBE_TOKEN: 上の工程で生成したトークンを貼り付け

* Jenkinsではsfdxのcodescanプラグインを使用して、下記のコマンドを実行します
  
  ```bash
  # CodeScan Pluginのインストール
  sfdx plugins:install sfdx-codescan-plugin
  # CodeScan Pluginの実行
  sfdx codescan:run --token [SonarQubeのプロジェクトトークン] --projectkey [プロジェクトキー] --server [SonarQubeサーバーのURL]
  ```

## X. リファレンス

* [Salesforce向けテストツール一覧](https://qiita.com/a_kuratani/items/0f832379d2fda3888c11)
