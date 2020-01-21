package org.usfirst.frc.team25.scouting.data.models

/**
 * Class that holds a comparison between two teams
 */
class Comparison(
	teamOne: Int,
	teamTwo: Int,
	comparator: Char
) {
	val lowerTeam =
		if (teamTwo > teamOne) teamOne else teamTwo
	val higherTeam =
		if (teamTwo > teamOne) teamTwo else teamOne
	val compareChar: Char =
		if (teamTwo > teamOne) comparator
		else when (comparator) {
			'<' -> '>'
			'>' -> '<'
			else -> '='
		}
	
	operator fun contains(teamNum: Int): Boolean =
		lowerTeam == teamNum || higherTeam == teamNum
	
	fun contradicts(secondComp: Comparison): Boolean =
		compareChar != '=' && betterTeam == secondComp.worseTeam && worseTeam == secondComp.betterTeam
	
	val betterTeam: Int = when (compareChar) {
		'<' -> higherTeam
		'>' -> lowerTeam
		else -> 0
	}
	
	val worseTeam: Int = when (compareChar) {
		'<' -> lowerTeam
		'>' -> higherTeam
		else -> 0
	}
	
	override fun toString(): String =
		"$lowerTeam $compareChar $higherTeam"
}
