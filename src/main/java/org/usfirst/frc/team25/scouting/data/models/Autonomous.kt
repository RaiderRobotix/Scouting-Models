package org.usfirst.frc.team25.scouting.data.models

data class Autonomous(
	val cellsScoredBottom: Int,
	val cellsScoredInner: Int,
	val cellsScoredOuter: Int,
	val cellPickupRpoint: Int,
	val cellPickupTrench: Int,
	val cellsDropped: Int,
	val crossInitLine: Boolean,
	val crossOpponentSector: Boolean
)