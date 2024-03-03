<!doctype html>
<html lang="ja" data-bs-theme="dark">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consent</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <style>
        html, body {
            height: 100vh;
        }

        .consent-form {
            min-width: 350px;
            max-width: 350px;
        }
    </style>
</head>
<body class="w-100 d-flex justify-content-center align-items-center">
<main class="consent-form border rounded shadow p-4">
    <form action="/consent" method="post">
        <h1 class="text-center fs-4 mb-4">
            Authorize Client
        </h1>
        <div class="mb-4 text-center">
            ${viewModel.clientName}
        </div>
        <div class="mb-4">
            <ul>
                <#list viewModel.scopes as scope>
                    <li>
                        <div>
                            <span class="fw-bold">${scope.name}</span>
                            ${scope.detail}
                        </div>
                    </li>
                </#list>
            </ul>
        </div>
        <div class="d-flex flex-row-reverse justify-content-between">
            <button class="btn btn-primary" type="submit" name="action" value="accept">Accept</button>
            <button class="btn btn-outline-secondary" type="submit" name="action" value="cancel">Cancel</button>
        </div>
    </form>
</main>
</body>
</html>