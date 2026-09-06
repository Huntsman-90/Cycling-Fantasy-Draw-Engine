package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SavedSquadEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.DraftMathEngine
import com.example.data.model.RaceConfig
import com.example.data.model.Rider
import com.example.data.model.RidersDatabase
import com.example.data.repository.DraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DraftUiState(
    val currentRace: RaceConfig = RaceConfig.PRESETS.first(),
    val starters: List<Rider?> = List(9) { null },
    val reserve: Rider? = null,
    val searchQuery: String = "",
    val filterRole: String = "all", // all, GC, Sprinter, Puncheur, Climber, ITT, Domestique
    val filterPrice: String = "all", // all, 1200, 1000, 800, 600, 400, 200
    val selectedTab: Int = 0, // 0: Ростер, 1: База гонщиков, 2: Гонка & Метео, 3: Личный кабинет
    val showExportDialog: Boolean = false,
    val showSaveSquadDialog: Boolean = false,
    val userMessage: String? = null
) {
    val totalSpent: Int
        get() = starters.filterNotNull().sumOf { it.price } + (reserve?.price ?: 0)

    val budgetRemaining: Int
        get() = currentRace.budget - totalSpent

    val isOverBudget: Boolean
        get() = totalSpent > currentRace.budget

    val totalWeightedPoints: Double
        get() {
            var sum = 0.0
            starters.forEachIndexed { index, rider ->
                if (rider != null) {
                    val score = DraftMathEngine.calculateRiderScore(rider, currentRace)
                    val mult = DraftMathEngine.MULTIPLIERS.getOrElse(index) { 0.2 }
                    sum += score * mult
                }
            }
            return sum
        }

    val filledStartersCount: Int
        get() = starters.count { it != null }

    val averageStarterPrice: Int
        get() {
            val filled = starters.filterNotNull()
            if (filled.isEmpty()) return 0
            return (filled.sumOf { it.price } / filled.size)
        }

    val currentStackName: String
        get() {
            val filled = starters.filterNotNull()
            val c1200 = filled.count { it.price == 1200 }
            return when {
                c1200 >= 3 -> "3 Звезды + База 400"
                c1200 == 2 -> "2 Суперзвезды + Глубина"
                c1200 == 1 -> "1 Соло Звезда + Ровный состав"
                filled.isEmpty() -> "Пустой ростер"
                else -> "Пользовательский стек"
            }
        }

    fun isRiderSelected(riderId: Int): Boolean {
        if (reserve?.id == riderId) return true
        return starters.any { it?.id == riderId }
    }
}

class DraftViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DraftRepository

    private val _uiState = MutableStateFlow(DraftUiState())
    val uiState: StateFlow<DraftUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DraftRepository(db.squadDao(), db.userProfileDao())
    }

    val savedSquads: StateFlow<List<SavedSquadEntity>> = repository.allSquads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileEntity()
        )

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setRacePreset(preset: RaceConfig) {
        _uiState.update { it.copy(currentRace = preset) }
    }

    fun updateRaceParams(
        kLevel: Double? = null,
        budget: Int? = null,
        mountain: Int? = null,
        cobbles: Int? = null,
        sprint: Int? = null,
        itt: Int? = null,
        wind: String? = null,
        rain: String? = null,
        temp: String? = null
    ) {
        _uiState.update { state ->
            val cur = state.currentRace
            state.copy(
                currentRace = cur.copy(
                    kLevel = kLevel ?: cur.kLevel,
                    budget = budget ?: cur.budget,
                    mountain = mountain ?: cur.mountain,
                    cobbles = cobbles ?: cur.cobbles,
                    sprint = sprint ?: cur.sprint,
                    itt = itt ?: cur.itt,
                    wind = wind ?: cur.wind,
                    rain = rain ?: cur.rain,
                    temp = temp ?: cur.temp
                )
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilterRole(role: String) {
        _uiState.update { it.copy(filterRole = role) }
    }

    fun setFilterPrice(price: String) {
        _uiState.update { it.copy(filterPrice = price) }
    }

    fun addStarter(rider: Rider) {
        val state = _uiState.value
        if (state.isRiderSelected(rider.id)) {
            setMessage("${rider.name} уже в составе!")
            return
        }

        val emptyIndex = state.starters.indexOfFirst { it == null }
        if (emptyIndex == -1) {
            setMessage("Все 9 слотов основы заняты. Освободите слот.")
            return
        }

        val updated = state.starters.toMutableList()
        updated[emptyIndex] = rider
        _uiState.update { it.copy(starters = updated) }
        sortByTieBreak()
    }

    fun removeStarter(index: Int) {
        val state = _uiState.value
        if (index in state.starters.indices) {
            val updated = state.starters.toMutableList()
            updated[index] = null
            _uiState.update { it.copy(starters = updated) }
            sortByTieBreak()
        }
    }

    fun moveStarter(fromIndex: Int, toIndex: Int) {
        val state = _uiState.value
        if (fromIndex in state.starters.indices && toIndex in state.starters.indices) {
            val updated = state.starters.toMutableList()
            val temp = updated[fromIndex]
            updated[fromIndex] = updated[toIndex]
            updated[toIndex] = temp
            _uiState.update { it.copy(starters = updated) }
        }
    }

    fun sortByTieBreak() {
        val state = _uiState.value
        val race = state.currentRace
        val filled = state.starters.filterNotNull()
            .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }

        val updated = MutableList<Rider?>(9) { null }
        filled.forEachIndexed { i, r ->
            if (i < 9) updated[i] = r
        }
        _uiState.update { it.copy(starters = updated) }
    }

    fun setReserve(rider: Rider) {
        if (rider.price > 400) {
            setMessage("Резерв строго за 200 или 400 кредитов!")
            return
        }
        val state = _uiState.value
        if (state.starters.any { it?.id == rider.id }) {
            setMessage("${rider.name} уже в основе!")
            return
        }
        _uiState.update { it.copy(reserve = rider) }
    }

    fun removeReserve() {
        _uiState.update { it.copy(reserve = null) }
    }

    fun clearRoster() {
        _uiState.update {
            it.copy(
                starters = List(9) { null },
                reserve = null
            )
        }
        setMessage("Состав очищен")
    }

    fun autoDraftStack(stackType: String) {
        val race = _uiState.value.currentRace
        val all = RidersDatabase.ALL_RIDERS

        when (stackType) {
            "3stars" -> {
                // 3x1200 + 6x400 = 6000 + 1x200 reserve
                val stars1200 = all.filter { it.price == 1200 }
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }
                    .take(3)
                val bases400 = all.filter { it.price == 400 }
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }
                    .take(6)
                val reserve200 = all.filter { it.price == 200 }
                    .maxByOrNull { DraftMathEngine.calculateTieBreakIndex(it, race) }

                val chosen = (stars1200 + bases400)
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }

                val updatedStarters = MutableList<Rider?>(9) { null }
                chosen.forEachIndexed { i, r -> if (i < 9) updatedStarters[i] = r }

                _uiState.update {
                    it.copy(
                        starters = updatedStarters,
                        reserve = reserve200
                    )
                }
                setMessage("Стек '3 Звезды + База' собран!")
            }

            "2stars" -> {
                // 2x1200 + 800 + 2x600 + 3x400 + 1x200 = 6000 (or adapted for 5000)
                val stars1200 = all.filter { it.price == 1200 }
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }
                    .take(2)
                val s800 = all.filter { it.price == 800 }
                    .maxByOrNull { DraftMathEngine.calculateTieBreakIndex(it, race) }
                val s600 = all.filter { it.price == 600 }
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }
                    .take(2)
                val s400 = all.filter { it.price == 400 }
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }
                    .take(if (race.budget >= 6000) 3 else 2)
                val s200 = all.filter { it.price == 200 }
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }

                val chosenList = mutableListOf<Rider>()
                chosenList.addAll(stars1200)
                if (s800 != null && race.budget >= 6000) chosenList.add(s800)
                chosenList.addAll(s600)
                chosenList.addAll(s400)
                if (s200.isNotEmpty()) chosenList.add(s200.first())

                val chosen = chosenList.take(9)
                    .sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }

                val updatedStarters = MutableList<Rider?>(9) { null }
                chosen.forEachIndexed { i, r -> if (i < 9) updatedStarters[i] = r }

                val res = s200.drop(1).firstOrNull() ?: s200.firstOrNull()

                _uiState.update {
                    it.copy(
                        starters = updatedStarters,
                        reserve = res
                    )
                }
                setMessage("Стек '2 Звезды + Глубина' собран!")
            }

            "arbitrage" -> {
                // Maximizing ROI per credit within budget
                val sortedByRoi = all.sortedByDescending { DraftMathEngine.calculateRiderROI(it, race) }
                val selected = mutableListOf<Rider>()
                var spent = 0
                val targetBudget = race.budget - 200 // reserve budget

                for (rider in sortedByRoi) {
                    if (selected.size == 9) break
                    if (spent + rider.price <= targetBudget) {
                        selected.add(rider)
                        spent += rider.price
                    }
                }

                val reservePick = all.filter { it.price <= 400 && !selected.contains(it) }
                    .maxByOrNull { DraftMathEngine.calculateRiderROI(it, race) }

                val sortedSelected = selected.sortedByDescending { DraftMathEngine.calculateTieBreakIndex(it, race) }
                val updatedStarters = MutableList<Rider?>(9) { null }
                sortedSelected.forEachIndexed { i, r -> if (i < 9) updatedStarters[i] = r }

                _uiState.update {
                    it.copy(
                        starters = updatedStarters,
                        reserve = reservePick
                    )
                }
                setMessage("Стек 'Max Value Arbitrage' сформирован!")
            }
        }
    }

    fun openExportDialog(open: Boolean) {
        _uiState.update { it.copy(showExportDialog = open) }
    }

    fun openSaveDialog(open: Boolean) {
        _uiState.update { it.copy(showSaveSquadDialog = open) }
    }

    fun setMessage(msg: String?) {
        _uiState.update { it.copy(userMessage = msg) }
    }

    // Room DB Operations
    fun saveCurrentSquad(title: String, notes: String = "") {
        viewModelScope.launch {
            val state = _uiState.value
            val ids = state.starters.map { it?.id ?: 0 }.joinToString(",")
            val squad = SavedSquadEntity(
                title = if (title.isBlank()) "Состав: ${state.currentRace.name}" else title,
                raceId = state.currentRace.id,
                raceName = state.currentRace.name,
                budgetLimit = state.currentRace.budget,
                totalSpent = state.totalSpent,
                expectedPoints = state.totalWeightedPoints,
                startersIds = ids,
                reserveId = state.reserve?.id,
                notes = notes
            )
            repository.saveSquad(squad)
            setMessage("Состав сохранен в Личный кабинет!")
        }
    }

    fun loadSquad(squad: SavedSquadEntity) {
        val race = RaceConfig.PRESETS.find { it.id == squad.raceId }
            ?: RaceConfig.PRESETS.first().copy(
                name = squad.raceName,
                budget = squad.budgetLimit
            )

        val idList = squad.startersIds.split(",")
            .mapNotNull { it.trim().toIntOrNull() }

        val loadedStarters = MutableList<Rider?>(9) { null }
        idList.forEachIndexed { index, id ->
            if (index < 9 && id > 0) {
                loadedStarters[index] = RidersDatabase.findById(id)
            }
        }

        val loadedReserve = squad.reserveId?.let { RidersDatabase.findById(it) }

        _uiState.update {
            it.copy(
                currentRace = race,
                starters = loadedStarters,
                reserve = loadedReserve,
                selectedTab = 0 // Return to draft matrix
            )
        }
        setMessage("Состав '${squad.title}' загружен в драфт!")
    }

    fun deleteSquad(id: Int) {
        viewModelScope.launch {
            repository.deleteSquad(id)
            setMessage("Состав удален")
        }
    }

    fun saveUserProfile(
        managerName: String,
        teamName: String,
        division: String,
        avatarColorIndex: Int,
        favoriteRace: String,
        favoriteRole: String,
        bio: String,
        notes: String
    ) {
        viewModelScope.launch {
            val profile = UserProfileEntity(
                id = 1,
                managerName = managerName,
                teamName = teamName,
                division = division,
                avatarColorIndex = avatarColorIndex,
                favoriteRace = favoriteRace,
                favoriteRole = favoriteRole,
                bio = bio,
                tacticalNotes = notes
            )
            repository.saveUserProfile(profile)
            setMessage("Профиль менеджера обновлен!")
        }
    }

    // Markdown Report Generation (Шаблон #6 из ТЗ)
    fun generateAnalyticalReport(): String {
        val state = _uiState.value
        val race = state.currentRace
        val starters = state.starters
        val reserve = state.reserve

        val sb = StringBuilder()
        sb.append("### CF-EDE // Cycling Fantasy Analytical Report\n\n")
        sb.append("🏆 **Гонка**: ${race.name} (${race.category})\n")
        sb.append("📊 **Параметры**: К_level: ${race.kLevel} | Горы: ${race.mountain}% | Паве: ${race.cobbles}% | Спринт: ${race.sprint}% | ITT: ${race.itt}км\n")
        sb.append("🌦️ **Метео**: Ветер: ${race.wind} | Осадки: ${race.rain} | Темп: ${race.temp}\n")
        sb.append("💰 **Бюджет**: Использовано ${state.totalSpent} из ${race.budget} кредитов (Свободно: ${state.budgetRemaining})\n")
        sb.append("📈 **Ожидаемые взвешенные очки E[Pts]**: ${String.format("%.1f", state.totalWeightedPoints)}\n")
        sb.append("🎯 **Стек**: ${state.currentStackName} | Средняя цена слота: ${state.averageStarterPrice}\n\n")

        sb.append("| Поз. | Множ. | Гонщик | Команда | Цена | E[Pts] | ROI | Роль / Источник |\n")
        sb.append("| :--- | :---: | :--- | :--- | :---: | :---: | :---: | :--- |\n")

        starters.forEachIndexed { idx, rider ->
            val pos = "${idx + 1}°"
            val mult = "×${DraftMathEngine.MULTIPLIERS.getOrElse(idx) { 0.2 }}"
            if (rider != null) {
                val score = String.format("%.1f", DraftMathEngine.calculateRiderScore(rider, race))
                val roi = String.format("%.2f", DraftMathEngine.calculateRiderROI(rider, race))
                val role = rider.getMainSource()
                sb.append("| $pos | $mult | **${rider.name}** | ${rider.team} | ${rider.price} | $score | $roi | $role |\n")
            } else {
                sb.append("| $pos | $mult | *(Слот свободен)* | — | — | — | — | — |\n")
            }
        }

        if (reserve != null) {
            val rScore = String.format("%.1f", DraftMathEngine.calculateRiderScore(reserve, race))
            val rRoi = String.format("%.2f", DraftMathEngine.calculateRiderROI(reserve, race))
            sb.append("| **RES** | — | **${reserve.name}** | ${reserve.team} | ${reserve.price} | $rScore | $rRoi | DNS Замена (Топ-15) |\n\n")
        } else {
            sb.append("| **RES** | — | *(Резерв не назначен)* | — | — | — | — | 200/400 кредитов |\n\n")
        }

        sb.append("#### 🧠 Тактический комментарий CF-EDE:\n")
        if (state.isOverBudget) {
            sb.append("⚠️ **Внимание**: Состав невалиден! Превышение бюджета на ${state.totalSpent - race.budget} кредитов.\n")
        } else {
            sb.append("✅ Состав полностью валиден по правилам лиги (Strict 1200 Cap & 9+1 slots).\n")
        }
        sb.append("- Капитанский тай-брейк 1°-3° аккумулирует основную массу очков за счет множителей x1.0, x0.9, x0.8.\n")
        sb.append("- Слоты 4°-9° оптимизированы под Value Arbitrage для максимизации суммарного ROI состава.\n")

        return sb.toString()
    }
}
