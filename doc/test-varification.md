# テスト自動化検証

Salesforceに適用可能なテスト自動化ツールを検証する目的

## 0. アジェンダ

## 1. 対象テストツール

|No.|ツール名|無料枠有無|特徴|参考価格|
|---|---|---|---|---|
|1|[code.scan](https://www.codescan.io/)|？|sonarcubeのPluginとして利用可能。|?|
|2|[Clayton](https://www.getclayton.com/)|？|リポジトリ登録型でプリリクエストで指摘、提案してくれる模様|最低$540/月|
|3|[PMD Apex](https://github.com/pmd/pmd)|OSS|無料|
|4|[salesforce-sonar-plugin](https://github.com/SalesforceFoundation/salesforce-sonar-plugin)|OSS|無料|

* Sonar Cubeとの親和性の観点から、code.scanを調査

## 2. 対象ツール調査詳細

[code.scan](https://www.codescan.io/)を対象として、静的解析の適用調査。

### 2-1. Sonar cubeの設定・起動

* docker-composeにsonar-cubeを追加。
  * [docker-compose.yml](../jenkins-sample/docker-compose.yml)
* 下記で起動してログイン
  
  ```bash
  docker-compose up -d
  ```

  * user: admin, pass: admin

|key|value|備考|
|project key|sfdx-sample||
|token(name)|sfdx-sample-codescan||
|token(val)|[XXX](../.sfdx/memo.md)|※別途連携|

* Sonar Scanerの導入
  * 検証用でローカルに導入 ([参考](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/))
  * プロジェクトのルートにsonar-project.propertiesを追加
  

### 2-2. Code scanのインストール

SFDXのプラグインよりインストール

```bash
sfdx plugins:install sfdx-codescan-plugin
```

### 2-3. Code scanの実行



## X. リファレンス

* [Salesforce向けテストツール一覧](https://qiita.com/a_kuratani/items/0f832379d2fda3888c11)
