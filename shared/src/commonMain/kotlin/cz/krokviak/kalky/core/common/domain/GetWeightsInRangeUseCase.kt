package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.core.common.repo.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

open class GetWeightsInRangeUseCase(
    private val personalInfoRepo: PersonalInfoRepo,
) {
    open suspend operator fun invoke(start: LocalDate, end: LocalDate): List<WeightEntry> =
        personalInfoRepo.getWeightsInRange(start, end)

    open fun observe(start: LocalDate, end: LocalDate): Flow<List<WeightEntry>> =
        personalInfoRepo.observeWeightsInRange(start, end)
}
