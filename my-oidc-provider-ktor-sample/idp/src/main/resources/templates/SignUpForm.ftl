<!doctype html>
<html lang="ja" data-bs-theme="dark">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign up</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <style>
        html, body {
            height: 100vh;
        }

        .sign-up-form {
            min-width: 350px;
            max-width: 350px;
        }

        .horizontal-line-heading {
            display: flex;
            align-items: center;
            margin: 1rem 0;
        }

        .horizontal-line-heading:before,
        .horizontal-line-heading:after {
            content: "";
            height: 1px;
            background: #ccc;
            flex-grow: 1;
        }

        .horizontal-line-heading:before {
            margin-right: 0.5rem;
        }

        .horizontal-line-heading:after {
            margin-left: 0.5rem;
        }
    </style>
    <script>
        async function webauthnSignUp() {
            const username = document.getElementById("username-passkey").value

            if (!username) {
                console.error("ユーザー名が入力されていません")
                return
            }

            const signUpRequest = await fetch("/webauthn/signUpRequest")

            if (signUpRequest.ok === false) {
                return
            }

            const {rpName, rpId, userId, challenge} = await signUpRequest.json()

            /**
             * @type {PublicKeyCredentialCreationOptions}
             */
            const options = {
                rp: {
                    name: rpName,
                    id: rpId
                },
                user: {
                    displayName: username,
                    id: base64ToUint8Array(userId),
                    name: username
                },
                challenge: base64ToUint8Array(challenge),
                pubKeyCredParams: [
                    {
                        type: "public-key",
                        alg: -7
                    },
                    {
                        type: "public-key",
                        alg: -257
                    }
                ],
                attestation: "none",
                excludeCredentials: [],
                authenticatorSelection: {
                    authenticatorAttachment: "platform",
                    userVerification: "required",
                    requireResidentKey: true
                }
            }

            const credential = await navigator.credentials.create({
                publicKey: options
            })

            if (credential instanceof PublicKeyCredential && credential.response instanceof AuthenticatorAttestationResponse) {
                const singUpResponse = await fetch("/webauthn/signUp", {
                    method: "POST",
                    body: new URLSearchParams({
                        username,
                        attestationObject: arrayBufferToBase64(credential.response.attestationObject),
                        clientDataJSON: arrayBufferToBase64(credential.response.clientDataJSON)
                    })
                })

                if (singUpResponse.ok) {
                    location.href = "/consent"
                } else {
                    console.log("Sign up failed.")
                }
            }
        }

        function base64ToUint8Array(value) {
            return new Uint8Array([...atob(value)].map(s => s.charCodeAt(0)))
        }

        function arrayBufferToBase64(value) {
            return btoa(String.fromCharCode(...new Uint8Array(value)))
        }
    </script>
</head>
<body class="w-100 d-flex justify-content-center align-items-center">
<main class="sign-up-form border rounded shadow p-4">
    <div class="mb-4">
        <label for="username" class="form-label">Username</label>
        <input type="text" class="form-control" name="username" id="username">
    </div>
    <div class="mb-4">
        <label for="password" class="form-label">Password</label>
        <input type="password" class="form-control" name="password" id="password">
    </div>
    <button class="btn btn-primary w-100" onclick="webauthnSignUp()">Sign up</button>
    <div class="horizontal-line-heading">Or</div>
    <div class="mb-4">
        <label for="username-passkey" class="form-label">Username</label>
        <input type="text" class="form-control" name="username-passkey" id="username-passkey">
    </div>
    <div>
        <button class="btn btn-primary w-100 d-flex justify-content-center" onclick="webauthnSignUp()">
            <span class="me-2">
                <svg width="20" height="20" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <g id="icon-passkey">
                        <circle id="icon-passkey-head" cx="10.5" cy="6" r="4.5" style="fill:currentColor"/>
                        <path id="icon-passkey-key"
                              d="M22.5,10.5a3.5,3.5,0,1,0-5,3.15V19L19,20.5,21.5,18,20,16.5,21.5,15l-1.24-1.24A3.5,3.5,0,0,0,22.5,10.5Zm-3.5,0a1,1,0,1,1,1-1A1,1,0,0,1,19,10.5Z"
                              style="fill:currentColor"/>
                        <path id="icon-passkey-body"
                              d="M14.44,12.52A6,6,0,0,0,12,12H9a6,6,0,0,0-6,6v2H16V14.49A5.16,5.16,0,0,1,14.44,12.52Z"
                              style="fill:currentColor"/>
                    </g>
                </svg>
            </span>
            Sign up with passkey
        </button>
    </div>
</main>
</body>
</html>