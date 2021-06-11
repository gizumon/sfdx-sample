# CI/CD検証

DevCondへの導入を目的として、JenkinsCI/CD pipelineの構築を検討

## 0. アジェンダ

* [1. CI/CDフロー](#1-CI/CDフロー)
  * [1-0. 環境](#1-0-環境)
  * [1-1. 認証](#1-1-認証)
  * [1-2. Jenkinsの設定](#1-2-jenkinsの設定)
* [X. APPENDIX](#X-APPENDIX)

## 1. CI/CDフロー

|No.|項目|備考|
|---|---|---|
|1-0|[1-0. 環境](1-0-環境)||
|1-1|[1-1. 認証](1-1-認証)||
|1-2|[1-1. ](1-1-認証)||
|1-3|[1-1. 認証](1-1-認証)||

### 1-1. JWTベースの認証フロー設定

[参考文献](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_auth_connected_app.htm)

* [こちら](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_auth_key_and_cert.htm)を参考に自己証明書を作成
* アプリケーションマネージャーから、[こちら](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_auth_connected_app.htm)の内容を参考に接続のアプリケーション設定を追

## 2. Jenkinsの設定

* 環境変数の追加
* NodeJSの導入
  * version v16.1.0
* Salesforce CLIの導入
  * npm install -g設定に追加

## X. APPENDIX

### X-0. Jenkins user

Jenkins User
|項目|値|備考|
|---|---|---|
|ユーザー名|user||
|パスワード|user||
|フルネーム|user||
|メールアドレス|user@sample.com||

* URL: http://localhost:18080/

<br>

Jenkins 環境変数
|環境変数|値|備考|
|---|---|---|
|SF_USERNAME|tomoatsu.sekikawa@dev.scsk.com||
|SF_INSTANCE_URL|https://login.salesforce.com||
|SF_CONSUMER_KEY|###|※memo参照|
|SERVER_KEY_CREDENTIALS_ID|SFDX_DEV|※任意|
|PACKAGE_NAME|||
|PACKAGE_VERSION|||
|TEST_LEVEL|||
|メールアドレス|user@sample.com||

<br>

Gitlab Access Token
|項目|値|備考|
|---|---|---|
|User|project_001_bot1|
|AccessToken|rUf9kpoS5P4JcnecCRy8||

<br>

NodeJS Installation
|項目|値|備考|
|---|---|---|
|Name|NodeJS_SFDX||
|Version|NodeJS 16.2.0||
|GlobalNPM|sfdx-cli@7.102||

### X-1. Jenkins動作確認環境の構築

* Dockerのインストール
  * Docker version 20.10.6, build 370c289
* Docker Composeのインストール

### その他リファレンス

* [Qiita: Jenkins × SFDX](https://qiita.com/takahito0508/items/05bf71a49ef93e0f1ad7)
