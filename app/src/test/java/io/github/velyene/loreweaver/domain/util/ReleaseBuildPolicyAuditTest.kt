package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReleaseBuildPolicyAuditTest {
	private val gradleFile = resolveGradleFile()

	@Test
	fun releaseBuildType_keepsExpectedHardeningFlags() {
		val contents = gradleFile.readText()
		val releaseBlock = Regex("release\\s*\\{([\\s\\S]*?)\\n\\s*}")
			.find(contents)
			?.groupValues
			?.get(1)
			?: error("Could not locate release buildTypes block in ${gradleFile.path}")

		assertTrue("Release build should explicitly disable debugging", releaseBlock.contains("isDebuggable = false"))
		assertTrue("Release build should explicitly disable JNI debugging", releaseBlock.contains("isJniDebuggable = false"))
		assertTrue("Release build should keep code shrinking enabled", releaseBlock.contains("isMinifyEnabled = true"))
		assertTrue("Release build should keep resource shrinking enabled", releaseBlock.contains("isShrinkResources = true"))
	}

	private fun resolveGradleFile(): File = sequenceOf(
		File("build.gradle.kts"),
		File("app/build.gradle.kts"),
		File("../build.gradle.kts"),
		File("../app/build.gradle.kts"),
	).firstOrNull(File::exists)
		?: File("build.gradle.kts")
}


