package buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class Utf8MojibakeAuditTask : DefaultTask() {
	@get:InputFile
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val scriptFile: RegularFileProperty

	@get:InputDirectory
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val repoRoot: DirectoryProperty

	@get:Input
	abstract val powerShellExecutable: Property<String>

	@get:Input
	abstract val useExecutionPolicyBypass: Property<Boolean>

	init {
		group = "verification"
		description = "Runs the repository UTF-8 and mojibake audit script."
	}

	@TaskAction
	fun runAudit() {
		val script = scriptFile.asFile.get()
		if (!script.isFile) {
			throw GradleException("Expected audit script was not found at ${script.absolutePath}.")
		}

		val rootDirectory = repoRoot.asFile.get()
		val command = mutableListOf(powerShellExecutable.get())
		if (useExecutionPolicyBypass.get()) {
			command += listOf("-ExecutionPolicy", "Bypass")
		}
		command += listOf(
			"-File",
			script.absolutePath,
			"-RepoRoot",
			rootDirectory.absolutePath
		)

		val process =
			ProcessBuilder(command)
				.directory(rootDirectory)
				.inheritIO()
				.start()

		val exitCode = process.waitFor()
		if (exitCode != 0) {
			throw GradleException("UTF-8/mojibake audit failed with exit code $exitCode.")
		}
	}
}

