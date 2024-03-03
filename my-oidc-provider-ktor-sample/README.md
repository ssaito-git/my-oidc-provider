# my-oidc-provider-ktor-sample

- idp
  OpenID Provider のサンプル実装。

- rp
  Relying Party のサンプル実装。

## 実行

OpenID Provider の実行。

```shell
./gradlew :my-oidc-provider-ktor-sample:idp:run
```

Relying Party の実行。

```shell
./gradlew :my-oidc-provider-ktor-sample:rp:run
```

Relying Party (http://localhost:8081) に接続する。

登録済みのユーザー。

| username | password |
|----------|----------|
| alice    | pass     |
| bob      | pass     |
