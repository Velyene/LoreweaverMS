package com.example.loreweaver.data.repository

/**
 * Constants for repository implementations to avoid magic numbers.
 * Addresses SonarQube S109 (magic numbers) issues.
 */
object RepositoryConstants {
	/**
	 * Maximum number of log entries to keep in the database.
	 * Older entries are automatically pruned.
	 */
	const val MAX_LOG_ENTRIES = 100
}
