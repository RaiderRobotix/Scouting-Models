package org.usfirst.frc.team25.scouting.data.models

import java.io.Serializable

data class ScoutEntry(
	val preMatch: PreMatch,
	val autonomous: Autonomous,
	val teleOp: TeleOp,
	val postMatch: PostMatch
)
