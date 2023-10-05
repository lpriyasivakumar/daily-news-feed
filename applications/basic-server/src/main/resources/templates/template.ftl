<#macro noauthentication title="Welcome">
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name=viewport content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="/style/reset.css">
        <link rel="stylesheet" href="/style/style.css">
        <link rel="icon" type="image/svg" href="/favicon.svg">
        <title>Web Application - Daily news feed</title>
    </head>
    <body>
    <header>
        <div class="container">
            Daily News Feed
        </div>
    </header>
    <main>
        <#nested>
    </main>
    <footer>
        <div class="container">
            <script>document.write("©" + new Date().getFullYear());</script>
        </div>
    </footer>
    </body>
    </html>
</#macro>