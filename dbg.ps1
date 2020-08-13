.\gradlew installDebug
if ($?) {
    adb shell am start -n me.davidteresi.elsewhere/me.davidteresi.elsewhere.MainActivity
    Start-Sleep -Seconds 0.2
    adb shell 'logcat --pid=$(pidof -s me.davidteresi.elsewhere)'
}