package org.usfirst.frc.team25.scouting.data

import org.apache.commons.math3.stat.descriptive.StatisticalSummary
import org.usfirst.frc.team25.scouting.data.StringProcessing.removeCommasBreaks
import org.usfirst.frc.team25.scouting.data.models.Autonomous
import org.usfirst.frc.team25.scouting.data.models.ScoutEntry
import org.usfirst.frc.team25.scouting.data.models.TeleOp
import java.io.File
import java.util.*

import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import kotlin.collections.HashMap

/**
 * Object model containing individual reports of teams in events and methods to process data and calculate team-based
 * statistics
 */
class TeamReport(val teamNum: Int) {
	
	@Transient
	val entries = ArrayList<ScoutEntry>()
	// HashMaps containing metric name and value pairings
	var teamName = ""
		private set
	private var frequentCommentStr = ""
	private var allComments: String? = null
	private val frequentComments = ArrayList<String>()
	private val attemptSuccessRates = HashMap<String, Double>()
	private val counts = HashMap<String, Int>()
	private val abilities = HashMap<String, Boolean>()
	private val statistics = HashMap<String, StatisticalSummary>()
	
	
	/**
	 * Processes the scout entries within the team report by filtering out no shows, calculating stats, and finding
	 * abilities and frequent comments
	 */
	fun processReport() {
		filterNoShow()
		findFrequentComments()
		calculateStats()
		findAbilities()
	}
	
	/**
	 * Removes scouting entries where the robot did not show up and increments the "no show" and "dysfunctional" counts
	 */
	private fun filterNoShow() {
		entries.removeIf { it.preMatch.noShow }
	}
	
	/**
	 * Increments a count metric by 1 and creates it if it currently does not exist
	 *
	 * @param metricName The count metric to increment
	 */
	private fun incrementCount(metricName: String) {
		if (counts.containsKey(metricName)) {
			counts[metricName] = getCount(metricName) + 1
		} else {
			counts[metricName] = 1
		}
	}
	
	/**
	 * Retrieves the value of the specified count metric
	 *
	 * @param metric String name of the desired metric
	 * @return The value of the count metric, 0 if the metric name does not exist
	 */
	fun getCount(metric: String): Int = counts[metric] ?: 0
	
	/**
	 * Calculates the counts, averages, standard deviations, and attempt-success rates of data in stored scouting
	 * entries, provided that data exists
	 */
	private fun calculateStats() {
		fun <T> Collection<T>.toDoubles() = when (first()) {
			is Number -> (this as Collection<Int>).map { it.toDouble() }
			is Boolean -> (this as Collection<Boolean>).map { if (it) 1.0 else 0.0 }
			else -> null
		}
		
		val autoList = entries.map { it.autonomous }
		for (prop in autoMetrics) {
			val summaryStatistics = SummaryStatistics()
			autoList.map { prop.call(it) }.toDoubles()?.forEach(summaryStatistics::addValue)
			statistics["auto${prop.name}"] = summaryStatistics.summary
		}
		
		val teleList = entries.map { it.teleOp }
		for (prop in TeleOp::class.members) {
			val summaryStatistics = SummaryStatistics()
			teleList.map { prop.call(it) }.toDoubles()?.forEach(summaryStatistics::addValue)
			statistics["tele${prop.name}"] = summaryStatistics.summary
		}
		
		for (prop in ScoutEntry::class.members) {
			val summaryStatistics = SummaryStatistics()
			entries.map { prop.call(it) }.toDoubles()?.forEach(summaryStatistics::addValue)
			statistics[prop.name] = summaryStatistics.summary
		}
	}
	
	/**
	 * Populates the frequent comment array with quick comments that appear at least 25% of the time in a team's
	 * scouting entries
	 * Also concatenates all custom comments made into the `allComments` string
	 */
	private fun findFrequentComments() {
		val commentFrequencies = HashMap<String, Int>()
		if (entries.size > 0) {
			for (key in entries[0].postMatch.robotQuickCommentSelections.keys) {
				commentFrequencies[key] = 0
				for (entry in entries) {
					if (entry.postMatch.robotQuickCommentSelections.containsKey(key)) {
						commentFrequencies[key] = commentFrequencies[key]!! + 1
					}
				}
			}
		}
		for (key in commentFrequencies.keys) {
			if (commentFrequencies[key]!! >= entries.size / 4.0) {
				frequentComments.add(key)
			}
		}
		for (comment in frequentComments) {
			frequentCommentStr += comment.removeCommasBreaks() + " \n"
		}
		allComments = ""
		for (entry in entries) {
			if (entry.postMatch.robotComment != "") {
				allComments += entry.postMatch.robotComment + "; "
			}
		}
	}
	
	/**
	 * Determines if teams are capable of intaking game pieces from the floor and their potential sandstorm and climb
	 * modes
	 */
	private fun findAbilities() {
		abilities["cargoFloorIntake"] = frequentComments.contains("Cargo floor intake")
		abilities["hatchPanelFloorIntake"] = frequentComments.contains("Hatch panel floor intake")
		for (entry in entries) { //            if (entry.autonomous().isSkillCapable()) {
//                abilities.put(SkillCapable,true)
//            }
		}
	}
	
	/**
	 * Finds the endgame HAB climb level for this team that results in the greatest expected endgame contribution
	 * Expected contribution is equal to the team's attempt-success rate for climbing a particular HAB level
	 * multiplied by the point value of that level
	 *
	 * @return The HAB climb level that yields the greatest expected contribution, 3 if the team has not climbed before
	 */
	val bestClimbLevel: Int by lazy {
		var bestLevel = 0
		var bestClimbPoints = 0.0
		val climbPointValues = intArrayOf(3, 6, 12)
		for (i in 0..2) {
			val potentialPoints = attemptSuccessRates[levelPrefixes[i] + "Climb"]!! * climbPointValues[i]
			if (potentialPoints >= bestClimbPoints) {
				bestClimbPoints = potentialPoints
				bestLevel = i + 1
			}
		}
		bestLevel
	}
	
	/**
	 * Generates a random sample of various metrics computed in `averages`, assuming a Normal distribution
	 * with standard deviations specified by the team's `standardDeviations`
	 *
	 * @return A HashMap with metric names as keys and their associated random values
	 */
	fun generateRandomSample(): HashMap<String, Double> {
		val randomSample = HashMap<String, Double>()
		statistics.forEach { (t, u) -> randomSample[t] = Stats.randomNormalValue(u.mean, u.standardDeviation) }
		return randomSample
	}
	
	/**
	 * Generates an easily-readable report with relevant stats on an team's capability
	 *
	 * @return A formatted string with relevant aggregate team stats
	 */
	val quickStatus: String by lazy {
		val statusString = StringBuilder("Team $teamNum")
		val append: (Any) -> StringBuilder = statusString::append
		if (teamName.isNotEmpty()) {
				append(" - $teamName")
		}
		statusString.append("\n\nSandstorm:")
		for (metric in autoMetrics) {
				append("\nAvg. ${metric.name}: ${Stats.round(statistics["auto$metric"]!!.mean, 2)}")
		}
		statusString.toString()
	}
	
	/**
	 * Adds entries to the scouting entry list of this team
	 *
	 * @param entry `ScoutEntry` to be added to this team report
	 */
	fun addEntry(entry: ScoutEntry) {
		// sanitize user input before adding entry
		entries.add(
			entry.copy( // All the data classes are intentionally immutable!
				postMatch = entry.postMatch.copy(
					robotComment = entry.postMatch.robotComment.removeCommasBreaks()
				)
			)
		)
	}
	
	/**
	 * Fetches the nickname of the team from the specified team list and assigns it to `teamName`
	 *
	 * @param dataLocation location of the TeamNameList file generated by `exportTeamList`
	 */
	fun autoGetTeamName(dataLocation: File) {
		val data = FileManager.getFileString(dataLocation)
		val values = data.split(",\n").toTypedArray()
		for (value in values) {
			if (value.split(",").toTypedArray()[0] == teamNum.toString()) {
				teamName = value.split(",").toTypedArray()[1]
				return
			}
		}
	}
	
	/**
	 * Retrieves the value of the specified ability metric
	 *
	 * @param metric String name of the desired metric
	 * @return The value of the ability metric, false if the metric name does not exist
	 */
	fun getAbility(metric: String): Boolean = abilities[metric] ?: false
	
	
	fun getAverage(metric: String): Double = statistics[metric]?.mean ?: 0.0
	
	fun getStandardDeviation(metric: String): Double = statistics[metric]?.standardDeviation ?: 0.0
	
	fun getAttemptSuccessRate(metric: String?): Double = attemptSuccessRates[metric]!!
	
	companion object {
		// Metric defined to assist with iterating over values
		private val autoMetrics = arrayOf(
			Autonomous::cellsScoredBottom,
			Autonomous::cellsScoredOuter,
			Autonomous::cellsScoredInner,
			Autonomous::crossInitLine
		)
		val teleMetricNames = arrayOf("cargoShipHatches", "rocketLevelOneHatches",
			"rocketLevelTwoHatches", "rocketLevelThreeHatches", "cargoShipCargo", "rocketLevelOneCargo",
			"rocketLevelTwoCargo", "rocketLevelThreeCargo", "numPartnerClimbAssists")
		val overallMetricNames = arrayOf("calculatedPointContribution",
			"calculatedSandstormPoints", "calculatedTeleOpPoints", "totalHatches", "totalCargo")
	}
	
}

