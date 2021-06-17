# CI/CD検証

DevCondへの導入を目的として、JenkinsCI/CD pipelineの構築を検討

## 0. アジェンダ

* [1. Jenkins設定](#1-jenkins設定)
  * [1-1. プラグイン](#1-1-プラグイン)
  * [1-2. グローバルツール設定](#1-2-グローバルツール設定)
  * [1-3. 環境変数](#1-3-環境変数)
  * [1-4. 認証情報](#1-4-認証情報)
  * [1-5. 入力パラメータ](#1-5-入力パラメータ)
* [2. CI/CDフロー](#2-CI/CDフロー)
  * [2-1. 認証](2-1-認証)
  * [2-2. ビルド(ソース変換)](2-2-ビルドソース変換)
  * [2-3. テスト](2-3-テスト)
  * [2-4. デプロイ](2-4-デプロイ)
* [X. APPENDIX](#X-appendix)

## 1. Jenkins設定

|No.|項目|備考|
|---|---|---|
|1-1|[1-1. プラグイン](#1-1-プラグイン)||
|1-2|[1-2. グローバルツール設定](#1-2-グローバルツール設定)||
|1-3|[1-3. 環境変数](#1-3-環境変数)||
|1-4|[1-4. 認証情報](#1-4-認証情報)||
|1-5|[1-5. 入力パラメータ](#1-5-入力パラメータ)||

### 1-1. プラグイン

NodeJS Pluginのインストール

* Dashboard > Jenkinsの管理 > プラグインの管理 へアクセス
* 利用可能タブを選択
* 検索欄に 'Node' と入力
* 'NodeJS Plugin' にチェック
* ページ下部の'Install without restart'を選択

### 1-2. グローバルツール設定

NodeJSをグローバルツールに設定__

* Dashboard > Jenkinsの管理 > Global Tool Configuration へアクセス
* NodeJS欄の'インストール済みNodeJS'ボタンを選択
  * ここでNodeJS欄が出てこない場合は、'プラグイン'の設定を見直してください
* NodeJS欄に下記として、設定を追加

|項目|値|備考|
|---|---|---|
|Name|NodeJS_SFDX||
|Version|NodeJS 16.2.0||
|GlobalNPM|sfdx-cli@7.102||

### 1-3. 環境変数

環境変数の追加

* グローバルプロパティ > 環境変数 をチェック
* 下記を設定
  * ※jenkinsfileのパラメータに持たしても良いかと思いますが一旦サンプルでは環境変数として作成

|環境変数|値|備考|
|---|---|---|
|SONARQUBE_SERVER_URL|http://XXX.XXX.XXX |SonarQubeが動作しているサーバーURL|

### 1-4. 認証情報

認証情報を追加

* Dashboard > Jenkinsの管理 > Manage Credentials > 追加
* 下記を追加

|認証名|種類|値|ID|備考|
|---|---|---|---|---|---|
|SalesforceCLI認証キーファイル|Secret file|※server.key|SFDX_SEVER_KEY|※Salesforce組織でJWTベアラーフロー認証設定時に使用したServerキー情報([参考](./result.md#2-x-jwtベアラーフロー認証の設定))|
|SalesforceCLI認証コンシューマーキー|Secret text|※consumer key|SFDX_SEVER_KEY|※Salesforce組織でJWTベアラーフロー認証設定後に生成されたコンシューマーキー([参考](./result.md#2-x-jwtベアラーフロー認証の設定))|

### 1-5. 入力パラメータ

下記はjenkinsfileで実行時に指定される想定のパラメータ

|項目|説明|備考|
|---|---|---|
|STAGE|リリース先環境||
|SFDX_USERNAME|デプロイを実行するSalesforceするユーザー名|デプロイの権限を持つユーザーを指定|
|GITLAB_URL|SFDXプロジェクト Gitlab URL||
|DEPLOY_BRANCH|デプロイ対象のブランチ||
|IS_RUN_APEX_TEST|Apexテストを実行するか|デフォルトはTRUE|
|IS_RUN_STATIC_ANALYSIS|静的解析を実行するか|デフォルトはFALSE|
|SONARQUBE_PRJ_KEY|Code.scan(SonarQube)のプロジェクトキー|IS_RUN_STATIC_ANALYSISがTrueの場合に必要|
|SONARQUBE_TOKEN|code.scanのプロジェクトアクセストークン|IS_RUN_STATIC_ANALYSISがTrueの場合に必要|

## 2. CI/CDフロー

|No.|項目|備考|
|---|---|---|
|2-1|[2-1. 認証](2-1-認証)||
|2-2|[2-2. ビルド(ソース変換)](2-2-ビルドソース変換)||
|2-3|[2-3. テスト](2-3-テスト)||
|2-4|[2-4. デプロイ](2-4-デプロイ)||

### 2-1. 認証

```bash
sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SFDX_SEVER_KEY} -a sfdx
```

* -u: ログインするユーザー名を指定
* -i: JWT認証設定時に取得したコンシューマーキーを指定
* -f: JWT認証設定時に設定したServerキー

<br>

[こちら](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_auth_key_and_cert.htm)を参考に自己証明書を作成

* アプリケーションマネージャーから、[こちら](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_auth_connected_app.htm)の内容を参考に接続のアプリケーション設定を追加
* [参考文献](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_auth_connected_app.htm)

<br>

### 2-2. ビルド(ソース変換)

```bash
sfdx force:source:convert -d ./release
```

スクラッチ組織で設定された情報はソース形式として、ローカルに反映される。  
上記のコマンドで、ソース形式からメタデータ形式に変換する

* -d: デプロイ対象のメタデータが存在するディレクトリ指定

<br>

### 2-3. テスト

TBD

<br>

### 2-4. デプロイ

```bash
// テスト実行したい場合
sfdx force:mdapi:deploy -w "-1" -d ./release -u sfdx  --testlevel=RunLocalTests

// テスト実行しない場合
sfdx force:mdapi:deploy -w "-1" -d ./release -u sfdx  --testlevel=NoTestRun
```

メタデータAPI形式でデプロイ  

* -w: タイムアウトまでの時間を指定("-1"は終了まで待つ)
* -d: デプロイ対象のメタデータが存在するディレクトリ指定
* -u: 認証時に設定したDevhub環境のエイリアスを指定
* --testlevel: デプロイ時に実行するテストを

<br>

<details><summary>※備考：</summary>

下記の様にソース形式のまま(メタデータ変換)しないままでも、リリースが可能。
ただし、不安定との評価が多々あったため、
本検証内ではソース形式でGIT管理し、リリース時にメタデータに変換する手法とする。

```bash
# メタデータ形式のデプロイ
sfdx force:mdapi:deploy

# ソース形式のデプロイ
sfdx force:source:deploy
```

将来的に、パッケージ開発の適用・移行が可能であれば、[こちら](https://developer.salesforce.com/docs/atlas.ja-jp.sfdx_dev.meta/sfdx_dev/sfdx_dev_dev2gp.htm)の2GPリリースの方がCI/CD適用性が高い様に見える

</details>

## X. APPENDIX

### X-0. Jenkins user

* ユーザー名：user
* パスワード：user

### X-1. Jenkins動作確認環境の構築

* Dockerのインストール
  * Docker version 20.10.6, build 370c289
* Docker Composeのインストール
  * docker-compose version 1.29.1, build c34c88b2

### その他リファレンス

* [Qiita: Jenkins × SFDX](https://qiita.com/takahito0508/items/05bf71a49ef93e0f1ad7)
