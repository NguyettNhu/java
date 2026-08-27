param(
    [switch]$SeedDemo
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path (Split-Path -Parent $projectRoot) ".env"

if (-not (Test-Path -LiteralPath $envFile)) {
    throw "Không tìm thấy file .env tại $envFile"
}

Get-Content -LiteralPath $envFile -Encoding UTF8 | ForEach-Object {
    $line = $_.Trim()
    if ($line.Length -eq 0 -or $line.StartsWith("#")) { return }

    $separator = $line.IndexOf("=")
    if ($separator -le 0) { return }

    $name = $line.Substring(0, $separator).Trim()
    $value = $line.Substring($separator + 1).Trim()
    if (($value.StartsWith('"') -and $value.EndsWith('"')) -or
        ($value.StartsWith("'") -and $value.EndsWith("'"))) {
        $value = $value.Substring(1, $value.Length - 2)
    }
    Set-Item -Path "Env:$name" -Value $value
}

if ($SeedDemo) {
    $env:APP_SEED_DEMO = "true"
}

Push-Location $projectRoot
try {
    & mvn spring-boot:run
    exit $LASTEXITCODE
} finally {
    Pop-Location
}
