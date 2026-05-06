// Top-level build file where you can add configuration options common to all subprojects/modules.

import buildlogic.Utf8MojibakeAuditTask

plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.kotlin.compose) apply false
	alias(libs.plugins.kotlin.serialization) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.hilt) apply false
}

val sqliteTempDir: File =
	layout.projectDirectory.dir(".gradle/sqlite-tmp").asFile.apply { mkdirs() }.canonicalFile
System.setProperty("org.sqlite.tmpdir", sqliteTempDir.absolutePath)

val isWindowsHost = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
val powerShellExecutableName = if (isWindowsHost) "powershell.exe" else "pwsh"

tasks.register<Utf8MojibakeAuditTask>("auditUtf8Mojibake") {
	scriptFile.set(layout.projectDirectory.file("scripts/audit_utf8_mojibake.ps1"))
	repoRoot.set(layout.projectDirectory)
	powerShellExecutable.set(powerShellExecutableName)
	useExecutionPolicyBypass.set(isWindowsHost)
}


