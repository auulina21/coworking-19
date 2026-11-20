$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$localMaven = Join-Path $PSScriptRoot '..\tools\apache-maven-3.9.11\bin\mvn.cmd'
if (Test-Path -LiteralPath $localMaven) { & $localMaven test compile exec:java }
else { & mvn test compile exec:java }
