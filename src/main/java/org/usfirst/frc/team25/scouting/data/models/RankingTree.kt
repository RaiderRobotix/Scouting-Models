package org.usfirst.frc.team25.scouting.data.models

import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

/**
 * Class representation of a tree with multiple nodes, each having a level that represents the position of a team on
 * a picklist
 * Note: This was not implemented when creating 2019 picklists and was mostly created as an experiment for an
 * improved comparison picklist during Summer 2018
 */
class RankingTree() : Cloneable {
	private var ranks = HashMap<Int, Int>()
	/**
	 * @return The maximum level of among all nodes in the tree
	 */
	var maxLevel = 0
		private set
	
	/**
	 * Initializes a RankingTree in which the each element of teamOrder is a numbered node,
	 * where the first element has the highest level and the last element has the lowest
	 *
	 * @param teamOrder An ArrayList that determines the levels and nodes of a new RankingTree
	 */
	constructor(teamOrder: ArrayList<Int>) : this() {
		for (i in teamOrder.indices) {
			ranks[teamOrder[i]] = teamOrder.size - i
		}
	}
	
	constructor(ranks: HashMap<Int, Int>) : this() {
		this.ranks = ranks.clone() as HashMap<Int, Int>
	}
	
	/**
	 * Creates a new node at level 0
	 *
	 * @param teamNum Label of the new node
	 */
	fun addNode(teamNum: Int) {
		if (!containsNode(teamNum)) {
			ranks[teamNum] = 0
		}
	}
	
	val treeHashMap: HashMap<Int, Int> get() = ranks.clone() as HashMap<Int, Int>
	
	/**
	 * @param teamNum The node label to be queried
	 * @return true if there is a node in the tree labeled with teamNum, false otherwise
	 */
	fun containsNode(teamNum: Int): Boolean {
		return ranks.containsKey(teamNum)
	}
	
	/**
	 * Creates a new node at the specified level
	 *
	 * @param teamNum Label of the new node
	 * @param level   Level of the new node
	 */
	fun addNode(teamNum: Int, level: Int) {
		if (!containsNode(teamNum)) {
			ranks[teamNum] = 0
			setLevel(teamNum, level)
		}
	}

	/**
	 * @param teamNum Node label of the node whose level is being changed
	 * @param level   The desired level of the node
	 */
	private fun setLevel(teamNum: Int, level: Int) {
		val level = level.coerceAtLeast(0)
		ranks[teamNum] = level
		maxLevel = maxLevel.coerceAtLeast(level)
	}

	/**
	 * Creates a new node at the same level of the old one
	 *
	 * @param newNodeNum Label of the new node
	 * @param oldNode    Node that the new node should be created alongside
	 * @throws Exception When oldNode does not exist in the tree
	 */
	@Throws(Exception::class)
	fun addNodeAlongside(newNodeNum: Int, oldNode: Int) {
		if (!containsNode(newNodeNum)) {
			ranks[newNodeNum] = getLevel(oldNode)
		}
	}

	/**
	 * Creates a new node at the one level above the old one
	 *
	 * @param newNodeNum Label of the new node
	 * @param oldNode    Node that the new node should be created above
	 * @throws Exception When oldNode does not exist in the tree
	 */
	@Throws(Exception::class)
	fun addNodeAbove(newNodeNum: Int, oldNode: Int) {
		if (!containsNode(newNodeNum)) {
			addNodeAlongside(newNodeNum, oldNode)
			promote(newNodeNum)
		}
	}

	/**
	 * Creates a new node one level below the old one
	 *
	 * @param newNodeNum Label of the new node
	 * @param oldNode    Node that the new node should be created below
	 * @throws Exception When oldNode does not exist in the tree
	 */
	@Throws(Exception::class)
	fun addNodeBelow(newNodeNum: Int, oldNode: Int) {
		if (!containsNode(newNodeNum)) {
			addNodeAlongside(newNodeNum, oldNode)
			demote(newNodeNum)
		}
	}

	/**
	 * @return A sorted (descending) string representation of the current tree, with
	 * a node on each line, followed by a comma and its level
	 */
	override fun toString(): String {
		ranks = ranks.sortByValue()
		val result = StringBuilder()
		for ((key, value) in ranks) {
			try {
				result.append(key).append(",").append(value).append("\n")
			} catch (e: Exception) { // TODO Auto-generated catch block
				e.printStackTrace()
			}
		}
		return result.toString()
	}

	/**
	 * @return A sorted (descending) ArrayList representation of the current tree,
	 * with the label of each node as an element of the list.
	 * Nodes with the same level are sorted randomly.
	 */
	fun toArrayList(): ArrayList<Int> {
		val result = ArrayList<Int>()
		ranks = ranks.sortByValue()
		for ((key) in ranks) {
			try {
				result.add(key)
			} catch (e: Exception) { // TODO Auto-generated catch block
				e.printStackTrace()
			}
		}
		return result
	}

	/**
	 * Determines the percentage of comparisons in a list that are being followed in the tree.
	 * Ignores comparisons in which either team does not have a node in the tree
	 *
	 * @param comparisons List of comparisons to be evaluated
	 * @return Compliance percentage: comparisons followed/valid comparisons * 100
	 */
	fun getCompliancePercent(comparisons: ArrayList<Comparison>): Double {
		var compliant = 0.0
		var validComparisons = 0
		for (comparison in comparisons) {
			try {
				if (isComparisonCompliant(comparison)) {
					compliant++
				}
				validComparisons++
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		return compliant / validComparisons * 100
	}

	/**
	 * Determines if a given comparison (e.g. A&gt;B) is followed in the tree
	 *
	 * @param comparison Comparison to be queried
	 * @return true if the comparison is followed in the tree, false otherwise
	 * @throws Exception if either team involved in the comparison does not have a node in the tree
	 */
	@Throws(Exception::class)
	fun isComparisonCompliant(comparison: Comparison): Boolean {
		return if (comparison.betterTeam == 0) {
			getLevel(comparison.higherTeam) == getLevel(comparison.lowerTeam)
		} else {
			getLevel(comparison.betterTeam) > getLevel(comparison.worseTeam)
		}
	}

	/**
	 * @param teamNum Node label to be queried
	 * @return The level of the specified node
	 * @throws Exception if the node labeled by teamNum does not exist
	 */
	@Throws(Exception::class)
	fun getLevel(teamNum: Int): Int {
		if (!containsNode(teamNum)) {
			throw Exception("Invalid level request for$teamNum")
		}
		return ranks[teamNum]!!
	}

	/**
	 * Increments the level of the given node by 1
	 *
	 * @param teamNum The node to be promoted
	 * @throws Exception if the node does not exist
	 */
	@Throws(Exception::class)
	fun promote(teamNum: Int) {
		setLevel(teamNum, getLevel(teamNum) + 1)
	}

	/**
	 * Decrements the level of the given node by 1,
	 * or increments the level of all nodes above it if it is level 0 currently
	 *
	 * @param teamNum The node to be demoted
	 * @throws Exception if the node does not exist
	 */
	@Throws(Exception::class)
	fun demote(teamNum: Int) {
		if (getLevel(teamNum) == 0) { //lowest level is 0
			for (key in ranks.keys) {
				if (key != teamNum) {
					promote(key)
				}
			}
		}
		setLevel(teamNum, getLevel(teamNum) - 1)
	}
	
	public override fun clone(): RankingTree {
		val clone = RankingTree()
		clone.ranks = this.ranks.clone() as HashMap<Int, Int>
		return clone
	}
	
	fun <K, V : Comparable<V>?> Map<K, V>.sortByValue(): LinkedHashMap<K, V> {
		val entries = Vector(this.entries)
		entries.sortWith(java.util.Map.Entry.comparingByValue())
		val sorted = LinkedHashMap<K, V>()
		entries.forEach(Consumer { sorted[it.key] = it.value })
		return sorted
	}
}