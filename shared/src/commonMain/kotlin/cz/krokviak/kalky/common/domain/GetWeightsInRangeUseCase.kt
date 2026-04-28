package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.common.repo.WeightEntry
import kotlinx.datetime.LocalDate

class GetWeightsInRangeUseCase(
    private val personalInfoRepo: PersonalInfoRepo,
) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): List<WeightEntry> =
        personalInfoRepo.getWeightsInRange(start, end)
}
