package org.usfirst.frc.team25.scouting.data.models

/**
 * Qualitative reflection on the robot's performance after a match
 * Not to be used for end game actions
 */
data class PostMatch(
	val comparison: Comparison,
	val pickNumber: Int = 0,
	val robotComment: String,
	val teleopFocus: String,
	val robotQuickCommentSelections: Map<String, Boolean> = mapOf()
)

