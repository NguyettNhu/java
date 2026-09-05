$ErrorActionPreference = "Stop"

$projectRef = "zgepbxyfzhvppbkeflfd"
$poolerHost = "aws-0-ap-south-1.pooler.supabase.com"
$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path (Split-Path -Parent $projectRoot) ".env"

if (-not (Test-Path -LiteralPath $envFile)) {
    throw "Không tìm thấy file .env tại $envFile"
}

$updates = [ordered]@{
    DATABASE_URL = "jdbc:postgresql://${poolerHost}:6543/postgres?pgbouncer=true&prepareThreshold=0&sslmode=require"
    DIRECT_URL = "jdbc:postgresql://${poolerHost}:5432/postgres?sslmode=require"
    DATABASE_USERNAME = "postgres.${projectRef}"
}

$lines = [System.Collections.Generic.List[string]]::new()
$updatedNames = [System.Collections.Generic.HashSet[string]]::new()

foreach ($line in Get-Content -LiteralPath $envFile -Encoding UTF8) {
    $matchedName = $null
    foreach ($name in $updates.Keys) {
        if ($line -match "^\s*$name\s*=") {
            $matchedName = $name
            break
        }
    }

    if ($null -ne $matchedName) {
        $lines.Add("$matchedName=$($updates[$matchedName])")
        [void]$updatedNames.Add($matchedName)
    }
    else {
        $lines.Add($line)
    }
}

foreach ($name in $updates.Keys) {
    if (-not $updatedNames.Contains($name)) {
        $lines.Add("$name=$($updates[$name])")
    }
}

$utf8WithoutBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllLines($envFile, $lines, $utf8WithoutBom)

Write-Host "Đã cập nhật cấu hình JDBC cho project Supabase $projectRef."
