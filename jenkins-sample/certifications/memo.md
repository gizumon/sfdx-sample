# Credentials memo

全て検証用の情報のみしか保持していないため、こちらにキー情報残します。

## Salesforce Developer Edition (検証ユーザー)

個人アカウントで作成しています

<details><summary>ユーザー</summary>

* url: https://scsk72-dev-ed.my.salesforce.com/
* user: jenkins@service.dev.com
* pass: Passw0rd
* 備考: 管理者権限があるため、ユーザー追加などはご自由にご利用ください。

</details>

<details><summary>JWTベアラー認証設定情報</summary>

CI/CD 設定に必要なファイル

* [server.key](./server.key)
* コンシューマーキー

  ```bash
  3MVG95mg0lk4batgzm6iNzpzgAZC0Vi6_8Ss60MiSnjWKMNUIdykjVDmagvaVhvfmBNAQGw.0McuNUvUrp2_g
  ```

その他、設定時に利用したファイル

* [server.crt](./server.crt)
* [server.csr](./server.csr)
* [server.pass.key](./server.pass.key)
  * Gitソースから外しているため、パス先に存在しない場合は別途メール連携等しているかと思います。

</details>

## Code.scan

個人アカウントで評価ライセンスを発行しています
`2021年7月15日`まで有効です

更新必要な場合には、[こちら](https://docs.codescan.io/hc/en-us/articles/360046504652-CodeScan-Self-Hosted-Downloads)から再発行ください。

<details><summary>Subscription Code (for downloads)</summary>

```bash
WzM0MzQ5NTUsImNlMGY4ZjRlNDhlYjA0NmYyYjY2NjVlZWE3MjY5NjdhIl0=
```

</details>

<details><summary>License</summary>

```bash
Q2hlY2tzdW06IFYzMjI3MzRjMzgyZmY1MDc1ZGVhNTIzMTYwODcxYzQ0ZGMKT3JnYW5pc2F0aW9uOiBTQ1NLClNlcnZlcjogMTI3LjAuMC4xClByb2R1Y3Q6IFNGClR5cGU6IEVWQUxVQVRJT04KTGljZW5zZVNlcnZlcjogaHR0cHM6Ly9saWNlbnNlLmNvZGUtc2Nhbi5jb20vaW5kZXgucGhwL2FwaS9hZGRQcm9qZWN0P2xpZD0zNDM0OTU1CkV4cGlyYXRpb246IDIwMjEtMDctMTUKTG9vcGJhY2s6IHRydWUKT3JnSWQ6IHNvbmFybGludA==
```

</details>

## Sonar Qube

<details><summary>ユーザー</summary>

デフォルトで存在するSonarQubeユーザーです

* user: admin
* pass: admin

</details>

<details><summary>プロジェクトアクセストークン</summary>

* dev:
  * sfdx-sample-codescan: db9967908b639654e526c31a794da2b718eac0dd

```bash
sonar-scanner \
  -Dsonar.projectKey=sfdx-sample \
  -Dsonar.sources=. \
  -Dsonar.host.url=https://code-analysis.develop.devcond-test.net \
  -Dsonar.login=db9967908b639654e526c31a794da2b718eac0dd
```

</details>
