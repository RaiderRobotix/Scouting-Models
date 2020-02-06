package org.usfirst.frc.team25.scouting.data.models

data class ScoutEntry(
	val preMatch: PreMatch? = null,
	val autonomous: Autonomous? = null,
	val teleOp: TeleOp? = null,
	val postMatch: PostMatch? = null
)
