package com.example

import com.example.data.model.DraftMathEngine
import com.example.data.model.RaceConfig
import com.example.data.model.RidersDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testRacePresets() {
        val tdf = RaceConfig.PRESETS.find { it.id == "tdf" }
        assertNotNull(tdf)
        assertEquals(6000, tdf!!.budget)

        val roubaix = RaceConfig.PRESETS.find { it.id == "roubaix" }
        assertNotNull(roubaix)
        assertEquals(5000, roubaix!!.budget)
    }

    @Test
    fun testRidersStrictPriceTiers() {
        val validPrices = setOf(200, 400, 600, 800, 1000, 1200)
        for (rider in RidersDatabase.ALL_RIDERS) {
            assertTrue(
                "Rider ${rider.name} has invalid price ${rider.price}",
                validPrices.contains(rider.price)
            )
            assertTrue("Max price cap is 1200", rider.price <= 1200)
        }
    }

    @Test
    fun testDraftMathEngineCalculations() {
        val tdf = RaceConfig.PRESETS.first()
        val pogacar = RidersDatabase.findById(1)!!
        val score = DraftMathEngine.calculateRiderScore(pogacar, tdf)
        assertTrue("Pogacar score should be positive", score > 0)

        val roi = DraftMathEngine.calculateRiderROI(pogacar, tdf)
        assertTrue("ROI should be positive", roi > 0)

        val tieBreak = DraftMathEngine.calculateTieBreakIndex(pogacar, tdf)
        assertTrue("TieBreak index should be positive", tieBreak > 0)
    }

    @Test
    fun testMultipliersCountAndValues() {
        assertEquals(9, DraftMathEngine.MULTIPLIERS.size)
        assertEquals(1.0, DraftMathEngine.MULTIPLIERS[0], 0.001)
        assertEquals(0.2, DraftMathEngine.MULTIPLIERS[8], 0.001)
    }
}
