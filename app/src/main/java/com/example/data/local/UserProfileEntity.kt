package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val managerName: String = "Александр (DS)",
    val teamName: String = "Aero Peloton Pro",
    val division: String = "WorldTour General Manager",
    val avatarColorIndex: Int = 0,
    val favoriteRace: String = "Tour de France",
    val favoriteRole: String = "GC Капитаны",
    val bio: String = "Специалист по CF-EDE и Value Arbitrage 200/400. Оптимизатор тай-брейков 1°-9°.",
    val tacticalNotes: String = "Стратегия для ГТ: брать минимум двух суперзвёзд (1200), закрывать основу жемчужинами по 400 (Дель Торо, Манье, Онли), резерв строго за 200 (Пеллиццари)."
)
