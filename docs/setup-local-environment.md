# ローカル検証環境の起動方法

ローカル検証環境の設定、起動方法です

## 必要動作環境

下記の環境で動作確認しています
(上手く動作しない場合には下記の環境でお試しください)

* Git
  * git version 2.30.1 (Apple Git-130)
* Dockerのインストール
  * Docker version 20.10.6, build 370c289
* Docker Composeのインストール
  * docker-compose version 1.29.1, build c34c88b2
* Visual Studio Code
  * Extensions
    * Salesforce CLI Integration v52.1.0
* Salesforce本番組織
  * Salesforce Developer Edition
    * URL: https://scsk72-dev-ed.lightning.force.com
  * サービスアカウント
    * user: jenkins@service.dev.com
    * pass: [XXXXXX](https://docs.google.com/spreadsheets/d/1Zs8IBC7-kRNVsTGKOT3AGzNp1-QCaXtfWyRjfTzUyvg/edit#gid=1214262664)

## 起動手順

ローカル検証環境の起動方法として、下記の２方法を準備しています。

* ローカル検証環境の起動方法
  * [起動手順](#起動手順)
    * [クイックスタートバージョン(推奨)](#クイックスタートバージョン推奨)
    * [手動設定バージョン](#手動設定バージョン)

### クイックスタートバージョン(推奨)

`JenkinsとSonarQube設定済みのコンテナを起動するバージョンです`

1. ローカルにソースを落としてきます
  
    ```bash
    git clone https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample.git
    ```

2. Jenkinsのディレクトリへ移動します

    ```bash
    # pwd: sfdx-sample
    cd jenkins-sample
    ```

3. コンテナを生成します

    ```bash
    # コンテナを生成 (--no-start: 生成のみ(起動なし))
    docker-compose up --no-start
    ```

4. バックアップのボリュームを復元し、コンテナに紐付けます

    ```bash
    # pwd: プロジェクトルート/jenkins-sample
    # コンテナのボリュームバックアップを復元
    bash restore-volume-bk.sh
    ```

    * volume-backupフォルダ内のデータをコンテナのボリュームに復元するコマンドが実行されます
    * Windowsの場合は、git bashで起動、もしくはファイル内のdockerコマンドを直接実行ください

5. コンテナを起動します

    ```bash
    # コンテナを起動
    docker-compose up -d
    # ログを確認
    docker-compose logs -f
    ```

    * 初期起動時は、dockerのメモリを結構使ってしまう様でたまに失敗します。。。
      ⇨ 失敗したら、一度コンテナ停止して、再度起動ください

      ```bash
      # コンテナ停止 (-v: ボリュームの削除)
      docker-compose down -v
      # コンテナ起動
      docker-compose up -d
      ```

      ⇨ どうしても起動しない様であれば、[dockerのメモリ許容上限を増やして(4GB以上推奨)](https://www.st-hakky-blog.com/entry/2020/05/08/220000)、再度実行お試しください

6. 起動した環境へブラウザでアクセスします。

    * Jenkins: http://localhost:18080/
      * ログインアカウント
        * username: user
        * password: user
    * SonarQube: http://localhost:9000/
      * ログインアカウント
        * username: admin
        * password: admin

7. sfdx-poc-sampleのジョブを実行します
    * sfdx-poc-sampleジョブのパラメータ付きビルドを選択 ([URL]( http://localhost:18080/job/sfdx-poc-sample/build?delay=0sec))
    * 下記のパラメータを変更して実行
      * IS_RUN_STATIC_ANALYSIS: TRUE
      * SONARQUBE_TOKEN: 406b02a06712e7fb3994ef8633a9d000a368a7b5
        * SonarQube連携が上手くいかない場合には、[手動設定バージョン 4.](./test-varification.md#3-2-code-scanの実行)からの手順を試し、より新しいSonarQubeプロジェクトを作成して、トークンを再生成しSONARQUBE＿TOKENへ適用ください

8. ジョブの成功とSonarQubeへの連携が完了できたらOKです
    * Gitlabの認証には個人のアカウントを使用しているため、失敗する場合には下記をご自身のアカウントに変更ください
      * [Credentials](http://localhost:18080/credentials/store/system/domain/_/) > GITLAB_USER
      * [Credentials](http://localhost:18080/credentials/store/system/domain/_/) > gitlab-integrator

### 手動設定バージョン

`JenkinsとSonarQubeのコンテナを起動し、手動で設定するバージョンです`

1. コンテナを起動します

    ```bash
    # コンテナを起動
    docker-compose up -d
    # ログを確認
    docker-compose logs -f
    ```

2. 起動したコンテナへアクセスします
    * Jenkins: http://localhost:18080/
      * ログインアカウント
        * 任意のアカウントを作成ください
    * SonarQube: http://localhost:9000/
      * ログインアカウント
        * username: admin
        * password: admin

3. Jenkinsの設定をします
    * [こちら](./cicd-varification.md#1-jenkins設定)(1. Jenkins設定)の手順実施します

4. SonarQubeを設定します
    * Projectsタブ > Create new project
    * 下記で作成します
      * Project key: sfdx-sample
      * Display name: sfdx-sample
      * Generate a token: sfdx-sample-codescan
    * 生成されたトークン情報をコピーします

5. CodeScanのライセンスを更新します
    * [Administration > CodeScan](http://localhost:9000/admin/settings?category=codescan) にアクセスします
    * CodeScan license欄を以下に更新します
      * [こちらのCodeScanライセンスを参照](../jenkins-sample/certifications/memo.md#codescan)
  
6. sfdx-poc-sampleのジョブを実行します
    * 3.で作成したsfdx-poc-sampleジョブのパラメータ付きビルドを選択 ([URL]( http://localhost:18080/job/sfdx-poc-sample/build?delay=0sec))
    * 下記のパラメータを変更して実行
      * IS_RUN_STATIC_ANALYSIS: TRUE
      * SONARQUBE_TOKEN: ※4.で取得したトークンを貼り付け

7. ジョブの成功とリリースが完了できたらOKです
    * Gitlabの認証には個人のアカウントを使用しているため、失敗する場合には下記をご自身のアカウントに変更ください
      * [Credentials](http://localhost:18080/credentials/store/system/domain/_/) > GITLAB_USER
      * [Credentials](http://localhost:18080/credentials/store/system/domain/_/) > gitlab-integrator

<br>

__NOTE:__

* SonarQube(CodeScan)に導入しているライセンスは2021/7/15までの評価ライセンスです
  * 期間過ぎている場合は[こちら](https://docs.codescan.io/hc/en-us/articles/360046504652-CodeScan-Self-Hosted-Downloads)より再発行ください
