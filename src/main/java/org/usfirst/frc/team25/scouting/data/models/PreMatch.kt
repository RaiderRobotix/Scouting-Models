package org.usfirst.frc.team25.scouting.data.models

/**
 * General information about a match and scout before it begins
 */
data class PreMatch(
	val scoutName: String,
	val teamNum: Int,
	val matchNum: Int,
	val scoutPos: String,
	val noShow: Boolean,
	val startingPos: String,
	val numStartingCells: Int
)