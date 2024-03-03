<!doctype html>
<html lang="ja" data-bs-theme="dark">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <style>
        html, body {
            height: 100vh;
        }

        .error {
            min-width: 350px;
            max-width: 350px;
        }
    </style>
</head>
<body class="w-100 d-flex justify-content-center align-items-center">
<main class="error border rounded shadow p-4">
    <h1 class="text-center fs-4 mb-4">
        ${viewModel.title}
    </h1>
    <div>
        ${viewModel.message}
    </div>
</main>
</body>
</html>