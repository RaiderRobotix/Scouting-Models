package org.usfirst.frc.team25.scouting.data.models

data class Autonomous(
	val cellsScoredBottom: Int = 0,
	val cellsScoredInner: Int = 0,
	val cellsScoredOuter: Int = 0,
	val cellPickupRpoint: Int = 0,
	val cellPickupTrench: Int = 0,
	val cellsDropped: Int = 0,
	val crossInitLine: Boolean = false,
	val crossOpponentSector: Boolean = false
)