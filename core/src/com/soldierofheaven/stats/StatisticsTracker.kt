package com.soldierofheaven.stats

import com.soldierofheaven.util.`interface`.Resettable

//this class should be a container for player statistics and should contain event listeners (and be registered at the start)
object StatisticsTracker : Resettable {
    var enemiesKilled: Int = 0
    var accuracy: Float = 0f
    var score: Int = 0
    var shotsFired: Int = 0
    var totalCurrency: Int = 0


    override fun reset() {
        shotsFired = 0
        totalCurrency = 0
        accuracy = 0f
        score = 0
        enemiesKilled = 0
    }
}
