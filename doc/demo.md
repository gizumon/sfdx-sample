# Demo

* 2021/06/11 内部向けデモシナリオ
* 2021/06/15 内部向けデモ v2

## 1. SFDXの紹介

* [参考リンク](https://buildersbox.corp-sansan.com/entry/2019/07/25/125610)

## 2. スクラッチ組織のデモ

### 2-1. スクラッチ組織を導入するための設定

1. 本番組織のDevHub機能を有効化

    * スクラッチ組織を利用するために、DevHub機能を有効化します。
    * 設定　⇨　[DevHub](https://scsk72-dev-ed.lightning.force.com/lightning/setup/DevHub/home)

2. JWTベアラー認証フローの設定

    * CI/CDでCLIベースの認証を可能にするためにJWTの設定を実施します。
    * 設定　⇨　[Lightning Experience アプリケーションマネージャ](https://scsk72-dev-ed.lightning.force.com/lightning/setup/NavigationMenus/home) ⇨ 新規接続アプリケーション

### 2-2. スクラッチ組織を利用した開発・運用方法

3. ローカルでログイン

    ```bash
    sfdx force:auth:web:login -a devhub-demo
    ```

4. スクラッチ組織の作成状況確認
  
   ```bash
   sfdx force:org:list
   ```

5. スクラッチ組織の作成

    ```bash
    sfdx force:org:create -s -f config/project-scratch-def.json -a demo
    ```

6. 作成したスクラッチ組織の確認と作成上限の確認
  
   ```bash
   # 組織の一覧表示
   sfdx force:org:list

   # 制限確認
   sfdx force:limits:api:display -u devhub-demo
   ```

    * Salesforceから「[有効なスクラッチ組織](https://scsk72-dev-ed.lightning.force.com/lightning/o/ActiveScratchOrg/list?filterName=Recent)」で状況確認することが可能
    * 有効なスクラッチ組織の数はEdition毎に異なっています。([参考](https://developer.salesforce.com/docs/atlas.ja-jp.230.0.sfdx_dev.meta/sfdx_dev/sfdx_dev_scratch_orgs_editions_and_allocations.htm))

7. スクラッチ組織の起動

    ```bash
    sfdx force:org:open -u demo
    ```

    * 生成されたURLにアクセスでスクラッチ組織環境への接続が完了
    * 起動時は`デフォルト設定`となっていることを確認

8. スクラッチ組織へローカルのソースを反映
  
    ```bash
    sfdx force:source:push
    ```

    * ローカルの変更が反映されていることを確認
      * ケースオブジェクトへtestのカスタム項目が追加されたローカルソース

9. スクラッチ組織へアクセス
    * ローカルのソースが反映されていることを確認

10. 設定を変更 (カスタム項目を追加し、ページレイアウトを編集など)

    ```bash
    sfdx force:source:pull
    ```

    * ケースオブジェクトへカスタム項目を追加
      * test-<日付> など
    * メタデータに変更が反映されていることを確認

11. オプション：Apex変更時はテストを実行

    ```bash
    sfdx force:apex:test:run -y
    ```

    * -y: 同期実行のオプション

12. Gitへ反映
  
  ```bash
  git add *
  git commit -m "Add demo commit"
  git push origin HEAD
  ```

## 3. Jenkins CI/CDの確認

1. CI/CDソースの確認

    * [ファイル格納先](https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample/-/blob/develop/jenkins/jenkins-sample/jenkinsfiles/sfdx-sample.groovy)

2. Jenkinsの起動とアクセス

    ```bash
    cd ./jenkins-sample
    docker-compose up
    ```

    * http://localhost:18080/ へアクセス

3. sfdx-sampleで、Pipelineを実行

4. 本番組織へ変更が反映されていることを確認
