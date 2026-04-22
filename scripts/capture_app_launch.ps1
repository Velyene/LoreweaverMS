$adb = 'C:\Users\tsang\AppData\Local\Android\Sdk\platform-tools\adb.exe'
if (-not (Test-Path $adb)) {
    Write-Error "ADB not found at $adb"
    exit 1
}

& $adb shell am force-stop com.example.encountertimer
& $adb logcat -c
& $adb shell am start -n com.example.encountertimer/.MainActivity
Start-Sleep -Seconds 3
$pid = (& $adb shell pidof com.example.encountertimer).Trim()
"PID=$pid"
if ($pid) {
    & $adb logcat -d --pid=$pid -v time
} else {
    'PID not found; dumping recent global logcat lines instead:'
    & $adb logcat -d -t 300 -v time
}

'--- TOP RESUMED ---'
$activities = & $adb shell dumpsys activity activities
$activities | Select-String -Pattern 'mResumedActivity','topResumedActivity','ResumedActivity','com.example.encountertimer' | ForEach-Object { $_.Line }

