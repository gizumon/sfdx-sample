# SFDXお試し

SFDXによるソースコードベースの開発を目的として検証

## 0. アジェンダ

* [1. 導入](#1-導入)
* [2. 初期設定手順](#2-初期設定手順)
* [3. APPENDIX](#3-appendix)
* [4. Tips](#4-tips)
* [5. Demo](#5-Demo)

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
    git commit
    ```

1. コミットをリモートリポジトリへ反映

    ```bash
    git push origin [任意のブランチ]
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
sfdx force:org:create -s -f config/project-scratch-def.json -a scratch

# Error message
ERROR running force:org:create:  This command requires a dev hub org username set either with a flag or by default in the config.
```

<details>
<summary>解決方法</summary>

* ログイン情報が正しくない可能性があります。再度下記のコマンドよりログインしてください。

  ```bash
  sfdx force:auth:web:login -d -a [任意の環境名]
  ```

</details>

### 4-2. スクラッチ組織の作成ができない。。。 (2)

```bash
sfdx force:org:create -s -f config/project-scratch-def.json -a scratch

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
          sfdx force:limits:api:display -u [設定したDevHubのエイリアス名]
          ```

</details>

### 4-3. スクラッチ組織の作成ができない。。。 (3)

```bash
sfdx force:org:create -s -f config/project-scratch-def.json -a scratch

# Error message
ERROR running force:org:create:  You do not have access to the [ScratchOrgInfo] object
```

<details>
<summary>解決方法</summary>

* スクラッチ組織を作成する権限がないユーザーでログインしていると思われます。下記コマンドより、再度認証お試しください。
  
  ```bash
  sfdx force:auth:web:login -d -a [任意の環境名]
  ```

</details>

### 4-4. メタデータAPIリリース時に一部のパーミッションが存在しないと弾かれてしまう。。。

```bash
sfdx force:mdapi:deploy -w -1 -d ./release -u sfdx --testlevel=RunLocalTests

# Error message
Error  release/profiles/Custom%3A Marketing Profile.profile  Custom%3A Marketing Profile  Unknown user permission: ManageCssUsers
Error  release/profiles/Custom%3A Sales Profile.profile      Custom%3A Sales Profile      Unknown user permission: ManageCssUsers
Error  release/profiles/Custom%3A Support Profile.profile    Custom%3A Support Profile    Unknown user permission: ManageCssUsers
```

<details><summary>解決方法</summary>

* スクラッチ組織と本番組織/サンドボックスのデフォルトのプロファイル設定が異なる模様。([参考](https://developer.salesforce.com/forums/?id=9060G0000005WHjQAM))

* 検証時点では、自動化に組み込み可能なワークアラウンドを見つけられていないため、下記をコメントアウトして、再度リリース下さい。
  * ※下記は既知のものです。その他にエラー出現するものありましたら、ご追加お願いします。

|ファイル名|項目名|備考|
|---|---|---|
|Admin.profile-meta.xml|CreateWorkBadgeDefinition||
|Admin.profile-meta.xml|FieldServiceAccess||
|Admin.profile-meta.xml|ManagePartners||
|Admin.profile-meta.xml|SendExternalEmailAvailable||
|Custom%3A Marketing Profile.profile-meta.xml|ManageCssUsers||
|Custom%3A Marketing Profile.profile-meta.xml|ViewGlobalHeader||
|Custom%3A Sales Profile.profile-meta.xml|ManageCssUsers||
|Custom%3A Sales Profile.profile-meta.xml|ViewGlobalHeader||
|Custom%3A Support Profile.profile-meta.xml|ManageCssUsers||
|Custom%3A Support Profile.profile-meta.xml|ViewGlobalHeader||

</details>

### 4-5. Sonar Qubeにプラグイン導入するとエラーで落ちてしまう。。。

```bash
2021.06.16 06:16:21 INFO  ce[][o.s.ce.app.CeServer] Compute Engine is stopped
2021.06.16 06:18:57 INFO  ce[][o.s.p.ProcessEntryPoint] Starting ce
2021.06.16 06:18:57 INFO  ce[][o.s.ce.app.CeServer] Compute Engine starting up...
2021.06.16 06:18:59 INFO  ce[][o.e.p.PluginsService] no modules loaded
2021.06.16 06:18:59 INFO  ce[][o.e.p.PluginsService] loaded plugin [org.elasticsearch.join.ParentJoinPlugin]
2021.06.16 06:18:59 INFO  ce[][o.e.p.PluginsService] loaded plugin [org.elasticsearch.percolator.PercolatorPlugin]
2021.06.16 06:18:59 INFO  ce[][o.e.p.PluginsService] loaded plugin [org.elasticsearch.transport.Netty4Plugin]
2021.06.16 06:19:08 INFO  ce[][o.s.s.e.EsClientProvider] Connected to local Elasticsearch: [127.0.0.1:9001]
2021.06.16 06:19:18 INFO  ce[][o.sonar.db.Database] Create JDBC data source for jdbc:postgresql://postgresql:5432/sonar
2021.06.16 06:19:18 INFO  ce[][o.s.p.ProcessEntryPoint] Hard stopping process
2021.06.16 06:19:19 WARN  ce[][o.s.p.ProcessEntryPoint$HardStopperThread] Can not stop in 1000ms
2021.06.16 06:19:22 INFO  ce[][o.s.s.p.ServerFileSystemImpl] SonarQube home: /opt/sonarqube
2021.06.16 06:19:22 INFO  ce[][o.s.c.c.CePluginRepository] Load plugins
2021.06.16 06:19:22 ERROR ce[][o.s.ce.app.CeServer] Compute Engine startup failed
java.lang.IllegalStateException: Fail to unzip plugin [codescanlang] /opt/sonarqube/extensions/plugins/sonar-codescanlang-plugin-4.5.6.jar to /opt/sonarqube/temp/ce-exploded-plugins/codescanlang
	at org.sonar.ce.container.CePluginJarExploder.explode(CePluginJarExploder.java:56)
	at org.sonar.core.platform.PluginLoader.defineClassloaders(PluginLoader.java:84)
	at org.sonar.core.platform.PluginLoader.load(PluginLoader.java:64)
	at org.sonar.ce.container.CePluginRepository.start(CePluginRepository.java:71)
	at org.sonar.core.platform.StartableCloseableSafeLifecyleStrategy.start(StartableCloseableSafeLifecyleStrategy.java:40)
	at org.picocontainer.injectors.AbstractInjectionFactory$LifecycleAdapter.start(AbstractInjectionFactory.java:84)
	at org.picocontainer.behaviors.AbstractBehavior.start(AbstractBehavior.java:169)
	at org.picocontainer.behaviors.Stored$RealComponentLifecycle.start(Stored.java:132)
	at org.picocontainer.behaviors.Stored.start(Stored.java:110)
	at org.picocontainer.DefaultPicoContainer.potentiallyStartAdapter(DefaultPicoContainer.java:1016)
	at org.picocontainer.DefaultPicoContainer.startAdapters(DefaultPicoContainer.java:1009)
	at org.picocontainer.DefaultPicoContainer.start(DefaultPicoContainer.java:767)
	at org.sonar.core.platform.ComponentContainer.startComponents(ComponentContainer.java:135)
	at org.sonar.ce.container.ComputeEngineContainerImpl.startLevel2(ComputeEngineContainerImpl.java:217)
	at org.sonar.ce.container.ComputeEngineContainerImpl.start(ComputeEngineContainerImpl.java:187)
	at org.sonar.ce.ComputeEngineImpl.startup(ComputeEngineImpl.java:45)
	at org.sonar.ce.app.CeServer$CeMainThread.attemptStartup(CeServer.java:160)
	at org.sonar.ce.app.CeServer$CeMainThread.run(CeServer.java:138)
Caused by: java.nio.channels.ClosedByInterruptException: null
	at java.base/java.nio.channels.spi.AbstractInterruptibleChannel.end(Unknown Source)
	at java.base/sun.nio.ch.FileChannelImpl.endBlocking(Unknown Source)
	at java.base/sun.nio.ch.FileChannelImpl.size(Unknown Source)
	at org.apache.commons.io.FileUtils.doCopyFile(FileUtils.java:1125)
	at org.apache.commons.io.FileUtils.copyFile(FileUtils.java:1076)
	at org.apache.commons.io.FileUtils.copyFile(FileUtils.java:1028)
	at org.sonar.ce.container.CePluginJarExploder.explode(CePluginJarExploder.java:52)
	... 17 common frames omitted
```

<details><summary>解決方法</summary>

確認中

</details>

## 5. Demo

デモシナリオは、[こちら](./demo.md)
