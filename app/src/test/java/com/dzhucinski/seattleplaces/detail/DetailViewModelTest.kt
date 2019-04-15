package com.dzhucinski.seattleplaces.detail

import androidx.lifecycle.Observer
import com.dzhucinski.seattleplaces.di.appModule
import com.dzhucinski.seattleplaces.repository.PlacesRepository
import com.dzhucinski.seattleplaces.search.SearchViewModel
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by Denis Zhuchinski on 4/15/19.
 */
class DetailViewModelTest : KoinTest {

    private val searchViewModel: SearchViewModel by inject()
    private val placesRepository: PlacesRepository by inject()

    @Mock
    lateinit var placesData: Observer<List<SearchViewModel.VenueItem>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        startKoin { appModule }
    }

    @Test
    fun `test search method`() {

    }
}