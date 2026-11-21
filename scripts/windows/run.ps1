# Shared preflight + command runner for the Windows clickable scripts.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File run.ps1 <check|start|verify|build>
param(
  [string]$Command = 'check'
)

$ErrorActionPreference = 'Stop'

function Say([string]$message) { Write-Host "`n[UHS] $message" -ForegroundColor Cyan }
function Die([string]$message) {
  Write-Host "`n[UHS] ERROR: $message" -ForegroundColor Red
  exit 1
}

$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$root = Split-Path -Parent (Split-Path -Parent $here)
Set-Location -LiteralPath $root
Say "Project folder: $root"

function Get-JavaVersionLine {
  $old = $ErrorActionPreference
  $ErrorActionPreference = 'Continue'
  try {
    return (& java -version 2>&1 | Select-Object -First 1 | Out-String).Trim()
  } finally {
    $ErrorActionPreference = $old
  }
}

# Java 25: trust JAVA_HOME, then common install folders, then the PATH.
Say 'Checking JDK 25 ...'
if (-not $env:JAVA_HOME) {
  $j = Get-Command java -ErrorAction SilentlyContinue
  if ($j -and $j.Source) {
    $candidate = Split-Path -Parent (Split-Path -Parent $j.Source)
    if (Test-Path (Join-Path $candidate 'bin\java.exe')) { $env:JAVA_HOME = $candidate }
  }
}
$bases = @(
  (Join-Path $env:ProgramFiles 'Eclipse Adoptium'),
  (Join-Path $env:ProgramFiles 'Java')
)
if (${env:ProgramFiles(x86)}) {
  $bases += (Join-Path ${env:ProgramFiles(x86)} 'Eclipse Adoptium')
}
if ($env:LOCALAPPDATA) {
  $bases += (Join-Path $env:LOCALAPPDATA 'Programs\Eclipse Adoptium')
  $bases += (Join-Path $env:LOCALAPPDATA 'Programs\Java')
}
foreach ($base in $bases) {
  if (-not $base -or -not (Test-Path -LiteralPath $base)) { continue }
  Get-ChildItem -LiteralPath $base -Directory -Filter 'jdk-25*' -ErrorAction SilentlyContinue | ForEach-Object {
    if (-not $env:JAVA_HOME -and (Test-Path (Join-Path $_.FullName 'bin\java.exe'))) {
      $env:JAVA_HOME = $_.FullName
    }
  }
}
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
  $env:PATH = "$($env:JAVA_HOME)\bin;$env:PATH"
}
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
  Die 'Java not found. Install Temurin JDK 25: https://adoptium.net/'
}
$javaLine = Get-JavaVersionLine
if ($javaLine -notmatch 'version\s+"?(\d+)') {
  Die "Cannot read the Java version from: '$javaLine'. Install Temurin JDK 25: https://adoptium.net/"
}
$major = [int]$Matches[1]
if ($major -ne 25) {
  Die "Java 25 required, found major version '$major'. Install Temurin JDK 25: https://adoptium.net/"
}
Write-Host "  Java OK: $javaLine"
if ($javaLine -match '25\.0\.2') {
  Write-Host '  Note: 25.0.2 can break Spotless. Use 25.0.5+ for the verify script.' -ForegroundColor Yellow
}

# Maven
Say 'Checking Maven ...'
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
  Die 'Maven not found. Install it with: winget install Apache.Maven'
}
$oldPreference = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
try {
  $mvnLine = (& mvn -version 2>&1 | Select-Object -First 1 | Out-String).Trim()
} finally {
  $ErrorActionPreference = $oldPreference
}
if (-not $mvnLine) { Die "Maven cannot start. Run 'mvn -version' to see the error." }
Write-Host "  Maven OK: $mvnLine"

function Read-Cfg([string]$key, [string]$file) {
  if (-not (Test-Path -LiteralPath $file)) { return $null }
  $pattern = '^\s*' + [regex]::Escape($key) + '\s*='
  $match = Select-String -LiteralPath $file -Pattern $pattern | Select-Object -Last 1
  if (-not $match) { return $null }
  $value = ($match.Line -split '=', 2)[1].Trim()
  $value = $value -replace '^["'']|["'']$', ''
  if ([string]::IsNullOrWhiteSpace($value)) { return $null }
  return $value
}

Say 'Checking database config ...'
$u = $env:DB_USERNAME
if (-not $u) { $u = Read-Cfg 'DB_USERNAME' '.env' }
if (-not $u) { $u = Read-Cfg 'db.user' 'db.properties' }
$h = $env:DB_HOST
if (-not $h) { $h = Read-Cfg 'DB_HOST' '.env' }
if (-not $h) { $h = Read-Cfg 'db.host' 'db.properties' }
$p = $env:DB_PORT
if (-not $p) { $p = Read-Cfg 'DB_PORT' '.env' }
if (-not $p) { $p = Read-Cfg 'db.port' 'db.properties' }
$d = $env:DB_DATABASE
if (-not $d) { $d = Read-Cfg 'DB_DATABASE' '.env' }
if (-not $d) { $d = Read-Cfg 'db.name' 'db.properties' }
if (-not $u) { Die 'Missing DB config. Copy .env.example to .env and set DB_USERNAME (or set db.user in db.properties).' }
if (-not $h) { $h = 'localhost' }
if (-not $p) { $p = '3306' }
if (-not $d) { $d = 'uhs_db' }
Write-Host ("  Target: {0}@{1}:{2}/{3} (password hidden)" -f $u, $h, $p, $d)
if (-not (Test-Path -LiteralPath '.env')) {
  Write-Host '  Note: .env not found; using environment variables or db.properties.' -ForegroundColor Yellow
}

# Native tools write to stderr; do not turn that into a terminating error.
$ErrorActionPreference = 'Continue'
switch ($Command.ToLowerInvariant()) {
  'check'  { Say 'All checks passed.' }
  'start'  { Say 'Starting the app ...'; & mvn -q exec:java }
  'verify' { Say 'Full build (tests, format, style) ...'; & mvn -B verify }
  'build'  { Say 'Quick build (no tests) ...'; & mvn -q -DskipTests package }
  default  { Die "Unknown command: $Command (use check, start, verify, or build)" }
}
$rc = $LASTEXITCODE
if ($null -eq $rc) { $rc = 0 }
if ($rc -ne 0) { Write-Host "`n[UHS] Command failed (exit $rc). See the output above." -ForegroundColor Red }
exit $rc
