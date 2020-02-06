package org.usfirst.frc.team25.scouting.data.models

/**
 * Container holding data from the tele-operated period
 * Includes endgame data
 */
data class TeleOp(
	val cellsScoredBottom: Int,
	val cellsScoredInner: Int,
	val cellsScoredOuter: Int,
	val cellsDropped: Int,
	val rotationControl: Boolean,
	val rotationOverspun: Boolean,
	val positionControl: Boolean,
	val attemptHang: Boolean,
	val successHang: Boolean,
	val rungLevel: Boolean,
	val assistingClimbTeamNum: Int,
	val assistedClimbs: Int
)
