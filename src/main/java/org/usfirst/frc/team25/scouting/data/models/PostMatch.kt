package org.usfirst.frc.team25.scouting.data.models

import lombok.Data
import lombok.experimental.Accessors
import java.util.*

/**
 * Qualitative reflection on the robot's performance after a match
 * Not to be used for end game actions
 */
data class PostMatch(
	val comparison: Comparison,
	val pickNumber: Int = 0,
	val robotComment: String,
	val autoFocus: String,
	val teleopFocus: String,
	val climbType: String
	
	
) {
	val robotQuickCommentSelections = HashMap<String, Boolean>()
}