package cz.krokviak.kalky.core.common

import cz.krokviak.kalky.core.camera.data.FoodAnalysisDto
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository
import cz.krokviak.kalky.core.network.FoodAnalysisClient
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FoodPhotoAnalyzerTest {

    private val fixedNow = Instant.parse("2026-05-08T10:00:00Z")
    private val fixedClock = object : Clock {
        override fun now(): Instant = fixedNow
    }

    private fun fakeImageStorage(path: String = "/tmp/fake.jpg") = object : ImageStorage {
        override suspend fun storeImageFile(imageBytes: ByteArray): String = path
        override suspend fun getImageBytes(imagePath: String): ByteArray = byteArrayOf()
    }

    @Test
    fun analyze_successPath_callsAllCallbacksAndCommitsLoadingFalse() = runTest {
        val placeholderId = 42L
        val analysisDto = FoodAnalysisDto(
            weight = 200,
            foodType = "fruit",
            title = "Avokádo",
            protein = 4,
            fat = 22,
            carbs = 12,
            healthScore = 8,
        )
        val analysisClient = mock<FoodAnalysisClient> {
            everySuspend { getAnalysis(any()) } returns analysisDto
        }
        val repository = mock<FoodRepository> {
            everySuspend { insertFoodItem(any()) } returns placeholderId
            everySuspend { updateFoodItem(any()) } returns Unit
            everySuspend { getFoodItem(any()) } returns FoodItemEntity(
                id = placeholderId,
                name = "Avokádo",
                calories = 262,
                protein = 4,
                fat = 22,
                carbs = 12,
                healthScore = 8,
                createdAt = fixedNow,
                updatedAt = fixedNow,
                localImagePath = "/tmp/fake.jpg",
                loading = true,
            )
        }
        val analyzer = FoodPhotoAnalyzer(repository, analysisClient, fakeImageStorage(), fixedClock)

        val placeholders = mutableListOf<FoodItemEntity>()
        val analysisCompletes = mutableListOf<FoodItemEntity>()
        val finalCommits = mutableListOf<FoodItemEntity>()
        var failedCalled = false

        val job = analyzer.analyze(
            scope = this,
            imageBytes = byteArrayOf(1, 2, 3),
            onPlaceholderInserted = { placeholders.add(it) },
            onAnalysisComplete = { analysisCompletes.add(it) },
            onFinalCommitted = { finalCommits.add(it) },
            onAnalysisFailed = { failedCalled = true },
        )

        advanceTimeBy(6_001)
        advanceUntilIdle()
        job.join()

        assertEquals(1, placeholders.size, "placeholder callback fires once")
        assertEquals(placeholderId, placeholders.single().id)
        assertEquals(1, analysisCompletes.size, "analysis-complete fires after success")
        assertEquals("Avokádo", analysisCompletes.single().name)

        assertEquals(1, finalCommits.size, "final-commit fires once")
        assertFalse(finalCommits.single().loading, "final item has loading=false")
        assertFalse(failedCalled, "failure callback not invoked on success")

        verifySuspend { repository.insertFoodItem(any()) }
        verifySuspend { analysisClient.getAnalysis(any()) }
    }

    @Test
    fun analyze_analysisFailure_callsFailedCallback_andStillCommitsLoadingFalse() = runTest {
        val placeholderId = 99L
        val analysisClient = mock<FoodAnalysisClient> {
            everySuspend { getAnalysis(any()) } returns null
        }
        val repository = mock<FoodRepository> {
            everySuspend { insertFoodItem(any()) } returns placeholderId
            everySuspend { updateFoodItem(any()) } returns Unit
            everySuspend { getFoodItem(any()) } returns null
        }
        val analyzer = FoodPhotoAnalyzer(repository, analysisClient, fakeImageStorage(), fixedClock)

        var placeholderCalled = false
        var analysisCompleteCalled = false
        var finalCommit: FoodItemEntity? = null
        var failedCalled = false

        val job = analyzer.analyze(
            scope = this,
            imageBytes = byteArrayOf(1),
            onPlaceholderInserted = { placeholderCalled = true },
            onAnalysisComplete = { analysisCompleteCalled = true },
            onFinalCommitted = { finalCommit = it },
            onAnalysisFailed = { failedCalled = true },
        )

        advanceTimeBy(6_001)
        advanceUntilIdle()
        job.join()

        assertTrue(placeholderCalled, "placeholder still inserted on analysis failure")
        assertFalse(analysisCompleteCalled, "no analysis-complete on null result")
        assertTrue(failedCalled, "failure callback invoked on null analysis")
        assertNotNull(finalCommit, "final commit still happens — loading flag must clear")
        assertFalse(finalCommit!!.loading)
    }

    @Test
    fun analyze_animationRunsConcurrentlyWithAnalysis_takesMaxOfBoth() = runTest {

        val analysisClient = mock<FoodAnalysisClient> {
            everySuspend { getAnalysis(any()) } returns FoodAnalysisDto(title = "Quick")
        }
        val repository = mock<FoodRepository> {
            everySuspend { insertFoodItem(any()) } returns 1L
            everySuspend { updateFoodItem(any()) } returns Unit
            everySuspend { getFoodItem(any()) } returns null
        }
        val analyzer = FoodPhotoAnalyzer(repository, analysisClient, fakeImageStorage(), fixedClock)

        val startTime = testScheduler.currentTime
        val job = analyzer.analyze(
            scope = this,
            imageBytes = byteArrayOf(),
            onPlaceholderInserted = {},
            onAnalysisComplete = {},
            onFinalCommitted = {},
        )
        advanceUntilIdle()
        job.join()
        val elapsed = testScheduler.currentTime - startTime

        assertTrue(elapsed >= 6_000, "pipeline waits for animation window (6s); was $elapsed ms")
    }
}
