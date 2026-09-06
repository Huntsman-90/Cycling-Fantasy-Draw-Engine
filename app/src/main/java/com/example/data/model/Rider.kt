package com.example.data.model

data class Rider(
    val id: Int,
    val name: String,
    val team: String,
    val price: Int, // Strict 200, 400, 600, 800, 1000, 1200
    val role: String, // "GC", "Sprinter", "Puncheur", "Climber", "ITT", "Domestique"
    val rLong: Double,
    val rShort: Double,
    val ftp: Double,
    val cobbles: Double,
    val sprint: Double,
    val itt: Double,
    val wetSkill: Double,
    val windSkill: Double,
    val heatResist: Double,
    val crashRisk: Double,
    val u25: Boolean = false,
    val isArbitrage: Boolean = false
) {
    fun getMainSource(): String {
        return when (role) {
            "GC" -> "GC + этапы"
            "Sprinter" -> "Спринтерские этапы"
            "Puncheur" -> "Холмы / паве"
            "Climber" -> if (u25) "U25 + Горная майка" else "Горные этапы / отрывы"
            "ITT" -> "Разделки (ITT)"
            else -> "Грегари / Топ-15"
        }
    }
}
