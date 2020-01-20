package org.usfirst.frc.team25.scouting.data.models

import lombok.Data
import lombok.experimental.Accessors


/**
 * Container holding data from the tele-operated period
 * Includes endgame data
 */
data class TeleOp(
	val cellsScoredBottom: Int,
	val cellsScoredInner: Int,
	val cellsScoredOuter: Int,
	val cellPickupRpoint: Int,
	val cellPickupTrench: Int,
	val cellsDropped: Int,
	val isRotationControl: Boolean,
	val isPositionControl: Boolean,
	val isAttemptHang: Boolean,
	val isSuccessHang: Boolean,
	val isHangAssisted: Boolean,
	val assistingClimbTeamNum: Int,
	val numPartnerClimbAssists: Int
)