<!doctype html>
<html lang="ja" data-bs-theme="dark">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <style>
        html, body {
            height: 100vh;
        }

        .content {
            min-width: 600px;
            max-width: 600px;
        }
    </style>
</head>
<body class="w-100 d-flex justify-content-center align-items-center flex-column">
<main class="content card text-center">
    <div class="card-body">
        <h1 class="card-title">
            Sample RP
        </h1>
        <#if viewModel.isSignIn()>
            <p class="card-text">サインインしています。</p>
            <p class="card-text">subject: ${viewModel.subject}</p>
            <p class="card-text">name: ${viewModel.name}</p>
            <a href="/sign-out" class="btn btn-outline-secondary">サインアウト</a>
        <#else>
            <p class="card-text">サインインしていません。</p>
            <a href="/login" class="btn btn-primary">サインイン</a>
        </#if>
    </div>
</main>
</body>
</html>