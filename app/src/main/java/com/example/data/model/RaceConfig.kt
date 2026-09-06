package com.example.data.model

import kotlin.math.round

data class RaceConfig(
    val id: String,
    val name: String,
    val category: String,
    val budget: Int, // 6000 for GT, 5000 for Monuments/1-week
    val kLevel: Double, // 1.30, 1.20, 1.00, 0.75, 0.45
    val mountain: Int, // 0..100%
    val cobbles: Int, // 0..100%
    val sprint: Int, // 0..100%
    val itt: Int, // 0..70 km
    val wind: String, // "calm", "moderate", "echelon"
    val rain: String, // "dry", "wet", "storm"
    val temp: String // "cold", "optimal", "heat"
) {
    companion object {
        val PRESETS = listOf(
            RaceConfig(
                id = "tdf",
                name = "Tour de France",
                category = "Гранд-тур (Лимит 6000)",
                budget = 6000,
                kLevel = 1.30,
                mountain = 85,
                cobbles = 15,
                sprint = 45,
                itt = 35,
                wind = "moderate",
                rain = "dry",
                temp = "heat"
            ),
            RaceConfig(
                id = "giro",
                name = "Giro d'Italia",
                category = "Гранд-тур (Лимит 6000)",
                budget = 6000,
                kLevel = 1.30,
                mountain = 90,
                cobbles = 20,
                sprint = 40,
                itt = 40,
                wind = "calm",
                rain = "wet",
                temp = "cold"
            ),
            RaceConfig(
                id = "vuelta",
                name = "Vuelta a España",
                category = "Гранд-тур (Лимит 6000)",
                budget = 6000,
                kLevel = 1.30,
                mountain = 95,
                cobbles = 0,
                sprint = 30,
                itt = 25,
                wind = "calm",
                rain = "dry",
                temp = "heat"
            ),
            RaceConfig(
                id = "roubaix",
                name = "Paris-Roubaix",
                category = "Монумент (Лимит 5000)",
                budget = 5000,
                kLevel = 1.20,
                mountain = 0,
                cobbles = 100,
                sprint = 20,
                itt = 0,
                wind = "echelon",
                rain = "wet",
                temp = "optimal"
            ),
            RaceConfig(
                id = "flanders",
                name = "Ronde van Vlaanderen",
                category = "Монумент (Лимит 5000)",
                budget = 5000,
                kLevel = 1.20,
                mountain = 30,
                cobbles = 70,
                sprint = 10,
                itt = 0,
                wind = "moderate",
                rain = "wet",
                temp = "optimal"
            ),
            RaceConfig(
                id = "dauphine",
                name = "Critérium du Dauphiné",
                category = "Недельная WT (Лимит 5000)",
                budget = 5000,
                kLevel = 1.20,
                mountain = 80,
                cobbles = 0,
                sprint = 30,
                itt = 30,
                wind = "calm",
                rain = "dry",
                temp = "optimal"
            )
        )
    }
}

object DraftMathEngine {
    // 9 starter multipliers for positions 1° to 9°
    val MULTIPLIERS = listOf(1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2)

    fun calculateRiderScore(rider: Rider, race: RaceConfig): Double {
        // 1. Base Class & Condition: R_base = 0.35 * R_long + 0.65 * R_short
        val rBase = (0.35 * rider.rLong) + (0.65 * rider.rShort)

        // 2. Terrain synergy (K_terrain)
        val pMountain = race.mountain / 100.0
        val pCobbles = race.cobbles / 100.0
        val pSprint = race.sprint / 100.0
        val pItt = (race.itt / 50.0).coerceAtMost(1.0)

        var terrainSynergy = 1.0
        terrainSynergy += (pMountain * (rider.ftp - 75.0) / 75.0) * 0.45
        terrainSynergy += (pCobbles * (rider.cobbles - 75.0) / 75.0) * 0.40
        terrainSynergy += (pSprint * (rider.sprint - 75.0) / 75.0) * 0.40
        terrainSynergy += (pItt * (rider.itt - 75.0) / 75.0) * 0.30
        val kTerrain = maxOf(0.65, terrainSynergy)

        // 3. Race Level multiplier (K_level)
        val kLevel = race.kLevel

        // 4. Meteorological module (K_weather)
        var weatherMod = 1.0
        if (race.wind == "echelon") {
            weatherMod += if (rider.windSkill >= 88.0) 0.12 else -0.15
        }
        if (race.rain == "wet") {
            weatherMod += if (rider.wetSkill >= 90.0) 0.08 else -0.08
        } else if (race.rain == "storm") {
            weatherMod += if (rider.wetSkill >= 90.0) 0.15 else -0.18
        }
        if (race.temp == "heat") {
            weatherMod += if (rider.heatResist >= 88.0) 0.05 else -0.10
        } else if (race.temp == "cold") {
            weatherMod += if (rider.wetSkill >= 90.0) 0.05 else -0.10
        }
        val kWeather = maxOf(0.60, weatherMod)

        // 5. Team Role multiplier (K_role)
        val kRole = when {
            rider.role == "GC" || (rider.role == "Sprinter" && rider.price >= 800) -> 1.0
            rider.role in listOf("Puncheur", "Climber", "Sprinter") -> 0.90
            rider.role == "ITT" -> 0.85
            rider.role == "Domestique" -> 0.45
            else -> 0.88
        }

        val rawScore = (rBase * 1.8) * kTerrain * kLevel * kWeather * kRole
        return round(rawScore * 10.0) / 10.0
    }

    fun calculateRiderROI(rider: Rider, race: RaceConfig): Double {
        val score = calculateRiderScore(rider, race)
        return round((score / rider.price.toDouble()) * 1000.0) / 1000.0
    }

    fun calculateTieBreakIndex(rider: Rider, race: RaceConfig): Double {
        return calculateRiderScore(rider, race) * (1.0 - rider.crashRisk)
    }
}
