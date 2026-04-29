param(
    [string]$AdbPath
)

function Resolve-AdbExecutable {
    param(
        [string]$ExplicitPath
    )

    $candidates = @()

    if ($ExplicitPath) {
        $candidates += $ExplicitPath
    }

    foreach ($sdkRoot in @($Env:ANDROID_SDK_ROOT, $Env:ANDROID_HOME)) {
        if ($sdkRoot) {
            $candidates += (Join-Path $sdkRoot 'platform-tools\adb.exe')
        }
    }

    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path $candidate)) {
            return $candidate
        }
    }

    $adbCommand = Get-Command adb -ErrorAction SilentlyContinue
    if ($adbCommand) {
        return $adbCommand.Source
    }

    throw "ADB not found. Set ANDROID_SDK_ROOT/ANDROID_HOME, put adb on PATH, or pass -AdbPath."
}

$adb = Resolve-AdbExecutable -ExplicitPath $AdbPath

& $adb shell am force-stop io.github.velyene.loreweaver
& $adb logcat -c
& $adb shell am start -n io.github.velyene.loreweaver/io.github.velyene.loreweaver.MainActivity
Start-Sleep -Seconds 3
$pid = (& $adb shell pidof io.github.velyene.loreweaver).Trim()
"PID=$pid"
if ($pid) {
    & $adb logcat -d --pid=$pid -v time
} else {
    'PID not found; dumping recent global logcat lines instead:'
    & $adb logcat -d -t 300 -v time
}

'--- TOP RESUMED ---'
$activities = & $adb shell dumpsys activity activities
$activities | Select-String -Pattern 'mResumedActivity','topResumedActivity','ResumedActivity','io.github.velyene.loreweaver','io.github.velyene.loreweaver.MainActivity' | ForEach-Object { $_.Line }

