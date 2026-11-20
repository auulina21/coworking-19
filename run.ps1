$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
New-Item -ItemType Directory -Force bin | Out-Null
$javaSources = @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
& javac -encoding UTF-8 --release 21 -d bin $javaSources
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
& java '-Dstdout.encoding=UTF-8' -cp bin ua.coworking.Main
