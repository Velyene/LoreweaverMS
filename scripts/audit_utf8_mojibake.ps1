[CmdletBinding()]
param(
    [string]$RepoRoot,
    [switch]$ShowCleanFiles
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if (-not $RepoRoot) {
	$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path
	$RepoRoot = (Resolve-Path (Join-Path $scriptDirectory '..')).Path
}

function Resolve-GitExecutable {
    $gitCommand = Get-Command git -ErrorAction SilentlyContinue
    if (-not $gitCommand) {
        throw 'Git was not found on PATH. Install Git or run this script from a shell where git is available.'
    }

    return $gitCommand.Source
}

function Test-IncludedTrackedTextFile {
    param(
        [string]$RelativePath
    )

    $normalized = $RelativePath -replace '\\', '/'
    if ($normalized -match '(^|/)(build|\.gradle|\.git|captures|out|\.externalNativeBuild|\.cxx)(/|$)') {
        return $false
    }

    $leaf = [System.IO.Path]::GetFileName($normalized)
    $extension = [System.IO.Path]::GetExtension($normalized).ToLowerInvariant()

    $includedExtensions = @(
        '.kt', '.kts', '.java', '.xml', '.yml', '.yaml', '.gradle', '.properties', '.pro', '.toml',
        '.md', '.txt', '.bat', '.cmd', '.ps1', '.gitignore', '.gitattributes', '.editorconfig'
    )
    $includedLeafNames = @('NOTICE', 'gradlew', 'gradlew.bat')

	return ($includedExtensions -contains $extension) -or ($includedLeafNames -contains $leaf)
}

function Join-CodePoints {
	param(
		[int[]]$CodePoints
	)

	return (-join ($CodePoints | ForEach-Object { [char]$_ }))
}

function Get-MojibakeMarkers {
	return @(
    [PSCustomObject]@{ Label = 'UTF8 BOM mojibake'; Literal = Join-CodePoints @(0x00EF, 0x00BB, 0x00BF) },
    [PSCustomObject]@{ Label = 'C3-prefixed mojibake'; Literal = Join-CodePoints @(0x00C3) },
    [PSCustomObject]@{ Label = 'C2-prefixed mojibake'; Literal = Join-CodePoints @(0x00C2) },
    [PSCustomObject]@{ Label = 'E2-20AC-prefixed mojibake'; Literal = Join-CodePoints @(0x00E2, 0x20AC) },
    [PSCustomObject]@{ Label = 'E2-02C6-prefixed mojibake'; Literal = Join-CodePoints @(0x00E2, 0x02C6) },
    [PSCustomObject]@{ Label = 'E2-2020-prefixed mojibake'; Literal = Join-CodePoints @(0x00E2, 0x2020) },
		[PSCustomObject]@{ Label = 'Replacement character'; Literal = [string][char]0xFFFD }
	)
}

function Format-LinePreview {
    param(
        [string]$Line
    )

    $singleLine = ($Line -replace "`t", '    ' -replace '\s+', ' ').Trim()
    if ($singleLine.Length -le 140) {
        return $singleLine
    }

    return $singleLine.Substring(0, 137) + '...'
}

$git = Resolve-GitExecutable
$utf8Strict = New-Object System.Text.UTF8Encoding($false, $true)
$markers = Get-MojibakeMarkers
$findings = New-Object System.Collections.Generic.List[object]
$missingTrackedFiles = New-Object System.Collections.Generic.List[string]
$scannedFiles = 0

Push-Location $RepoRoot
try {
    $trackedFiles = & $git --no-pager ls-files
    if ($LASTEXITCODE -ne 0) {
        throw 'Unable to enumerate tracked files with git ls-files.'
    }

    foreach ($relativePath in $trackedFiles) {
        if (-not (Test-IncludedTrackedTextFile -RelativePath $relativePath)) {
            continue
        }

        $fullPath = Join-Path (Get-Location) $relativePath
        if (-not (Test-Path $fullPath -PathType Leaf)) {
            $missingTrackedFiles.Add($relativePath)
            continue
        }

        $scannedFiles += 1

        try {
            $bytes = [System.IO.File]::ReadAllBytes($fullPath)
            $text = $utf8Strict.GetString($bytes)
        } catch {
            $findings.Add([PSCustomObject]@{
                    Type = 'Invalid UTF-8'
                    File = $relativePath
                    Line = $null
                    Marker = $null
                    Detail = $_.Exception.Message
                })
            continue
        }

        $lines = [System.Text.RegularExpressions.Regex]::Split($text, "`r?`n")
        for ($lineIndex = 0; $lineIndex -lt $lines.Count; $lineIndex++) {
            $line = $lines[$lineIndex]
            foreach ($marker in $markers) {
                if ($line.Contains($marker.Literal)) {
                    $findings.Add([PSCustomObject]@{
                            Type = 'Mojibake'
                            File = $relativePath
                            Line = $lineIndex + 1
                            Marker = $marker.Literal
                            Detail = "$($marker.Label): $(Format-LinePreview -Line $line)"
                        })
                }
            }
        }

        if ($ShowCleanFiles) {
            Write-Host "[OK] $relativePath"
        }
    }
} finally {
    Pop-Location
}

if ($missingTrackedFiles.Count -gt 0) {
    Write-Warning ("Skipped {0} tracked path(s) that are not present in the working tree." -f $missingTrackedFiles.Count)
}

if ($findings.Count -eq 0) {
    Write-Host ("UTF-8/mojibake audit passed. Scanned {0} tracked text file(s)." -f $scannedFiles) -ForegroundColor Green
    exit 0
}

Write-Host ("UTF-8/mojibake audit found {0} issue(s) across {1} tracked text file(s)." -f $findings.Count, $scannedFiles) -ForegroundColor Red
foreach ($finding in $findings) {
    if ($finding.Type -eq 'Invalid UTF-8') {
        Write-Host ("[INVALID UTF-8] {0}`n  {1}" -f $finding.File, $finding.Detail) -ForegroundColor Red
        continue
    }

    Write-Host ("[MOJIBAKE] {0}:{1}`n  {2}" -f $finding.File, $finding.Line, $finding.Detail) -ForegroundColor Yellow
}

exit 1

