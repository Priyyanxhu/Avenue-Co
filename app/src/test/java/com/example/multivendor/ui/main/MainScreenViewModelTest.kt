package com.example.multivendor.ui.main

import com.example.multivendor.data.DataRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = MainScreenViewModel(FakeMyModelRepository())
        // First emission is always Loading (initial value of stateIn).
        assertEquals(MainScreenUiState.Loading, viewModel.uiState.first())
    }

    @Test
    fun uiState_onDataEmitted_isSuccess() = runTest {
        val viewModel = MainScreenViewModel(FakeMyModelRepository())
        advanceUntilIdle()
        val success = viewModel.uiState.first { it is MainScreenUiState.Success }
        assertTrue(success is MainScreenUiState.Success)
        assertEquals(listOf("Sample"), (success as MainScreenUiState.Success).data)
    }

    @Test
    fun uiState_onUpstreamError_isError() = runTest {
        val viewModel = MainScreenViewModel(FailingRepository())
        advanceUntilIdle()
        val error = viewModel.uiState.first { it is MainScreenUiState.Error }
        assertTrue(error is MainScreenUiState.Error)
    }
}

private class FakeMyModelRepository : DataRepository {
    override val data: Flow<List<String>> = flow { emit(listOf("Sample")) }
}

private class FailingRepository : DataRepository {
    override val data: Flow<List<String>> = flow { throw IllegalStateException("boom") }
}
